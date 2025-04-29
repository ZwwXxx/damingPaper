package com.ruoyi.web.controller.quiz.student;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.dm.quiz.domain.ai.Message;
import com.dm.quiz.domain.ai.RequestMessage;
import com.ruoyi.common.core.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;

import okhttp3.*;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.*;

@Slf4j
@RestController
@RequestMapping("/quiz/student/chat")
@CrossOrigin(origins = "http://localhost:7777") // 允许的源
public class AiController {
  @Value("${wenxin.akey}")
  private String akey;

  @Value("${wenxin.skey}")
  private String skey;

  private volatile boolean isCompleted = false;
  private SseEmitter currentEmitter; // 存储当前的 SseEmitter，以便后续停止使用

  // 创建全局共享的HTTP客户端实例，避免每次请求创建新实例
  private final OkHttpClient httpClient = new OkHttpClient.Builder()
      .connectTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .readTimeout(60, TimeUnit.SECONDS)
      .build();

  // 缓存accessToken，避免频繁请求
  private String accessToken;
  private long tokenExpireTime = 0;

  // 且该map为服务器共享的全局事件池
  @DeleteMapping("/clear-context")
  public AjaxResult clearContext(HttpSession session) {
    // 针对当前session Id的 record记录
    RequestMessage req = (RequestMessage) session.getAttribute("req");
    log.info("正在清理当前的session{}:", req);
    if (req != null) {
      session.removeAttribute("req");
      log.info("清除成功，当前值为" + session.getAttribute("req"));
    }
    // 由于是多线程，我们用sychronize锁清除req里的所有message，后续线程将读取不到
    return AjaxResult.success("上下文已清除");
  }

  @DeleteMapping("/stopStream")
  public AjaxResult stopStream() {
    if (currentEmitter != null) {
      isCompleted = true; // 设置标志位为 true，停止线程
      currentEmitter.complete(); // 关闭当前的 SseEmitter
      currentEmitter = null;
      return AjaxResult.success("流已停止");
    } else {
      return AjaxResult.error("没有正在运行的流");
    }
  }

  // 增加一个定义最大缓冲区大小的常量
  private static final int MAX_BUFFER_SIZE = 3; // 最大缓冲消息数量

  @GetMapping(value = "/openSseRaw", produces = "text/event-stream;charset=UTF-8")
  public SseEmitter handleStreamX(String content, HttpSession session) throws IOException {
    // 设置更长的超时时间避免超时断开
    currentEmitter = new SseEmitter(180000L); // 3分钟超时

    // 设置响应头
    currentEmitter.send(SseEmitter.event().name("init").data("连接成功").id("0"));

    // 设置心跳以保持连接
    currentEmitter.onCompletion(() -> log.info("SSE 完成"));
    currentEmitter.onTimeout(() -> log.info("SSE 超时"));
    currentEmitter.onError((ex) -> log.error("SSE 错误: ", ex));

    RequestMessage req = (RequestMessage) session.getAttribute("req");
    if (req == null) {
      log.info("session记录为空");
      req = new RequestMessage();
    }
    Message user = new Message("user", content);
    req.addMessage(user);

    String jsonString = JSON.toJSONString(req);

    // 设置请求体和请求
    MediaType mediaType = MediaType.parse("application/json");
    RequestBody requestBody = RequestBody.create(mediaType, jsonString);
    String baseUrl = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/ernie_speed";
    Request request = new Request.Builder()
        .url(baseUrl + "?access_token=" + getAccessToken())
        .method("POST", requestBody)
        .addHeader("Content-Type", "application/json")
        .build();

    // 异步执行请求
    isCompleted = false;
    final RequestMessage finalReq = req;

    // 使用线程池替代直接创建线程
    CompletableFuture.runAsync(() -> {
      log.info("开始处理流式响应");
      Integer totalTokens = 0;
      StringBuilder result = new StringBuilder();

      try {
        Response response = httpClient.newCall(request).execute();
        if (!response.isSuccessful()) {
          log.error("流式请求异常: " + response);
          currentEmitter.completeWithError(new RuntimeException("请求失败，状态码: " + response.code()));
          return;
        }

        try (
            InputStream inputStream = response.body().byteStream();
            // 增加缓冲区大小提高读取效率
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8192)) {

          String line;
          while ((line = reader.readLine()) != null && !isCompleted) {
            if (line.startsWith("data: ")) {
              String dataToWrite = line.substring(6).trim(); // 去掉前缀和空格
              if (currentEmitter == null) {
                return;
              }

              // 使用异常处理来避免JSON解析失败导致的中断
              try {
                JSONObject jsonObject = JSON.parseObject(dataToWrite);
                totalTokens = jsonObject.getJSONObject("usage").getInteger("total_tokens");
                String buffer = jsonObject.getString("result");
                // 修复Unicode转义问题
                buffer = buffer.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
                result.append(buffer);

                // 立即发送消息，不等待缓冲区填满
                currentEmitter.send(SseEmitter.event()
                    .name("message")
                    .data(dataToWrite, org.springframework.http.MediaType.APPLICATION_JSON)
                    .id(String.valueOf(System.currentTimeMillis())));

                boolean isEnd = (boolean) jsonObject.get("is_end");
                if (isEnd) {
                  break;
                }
              } catch (Exception e) {
                log.warn("处理消息时发生错误: " + e.getMessage());
                // 继续处理下一条消息，而不是终止整个流
              }
            }
          }
        }
      } catch (Exception e) {
        log.error("处理流式响应时发生异常: ", e);
        if (currentEmitter != null) {
          currentEmitter.completeWithError(e);
        }
      } finally {
        log.info("流式响应处理完成");
        isCompleted = true;
        synchronized (finalReq) {
          recordInfo(totalTokens, String.valueOf(result), finalReq, session);
        }
        if (currentEmitter != null) {
          currentEmitter.complete();
        }
      }
    });

    return currentEmitter;
  }

  private void recordInfo(Integer totalTokens, String result, RequestMessage req, HttpSession session) {
    // 记录一下聊天记录
    Message assistant = new Message("assistant", result);
    req.addMessage(assistant);
    // 同时将该信息存入session上下文
    // 记录存储前的状态

    log.info("正在设置聊天记录到session");
    synchronized (session) {
      session.setAttribute("req", req); // 使用相同的键名
    }
    log.info("设置聊天记录到session成功");

    BigDecimal money = BigDecimal.valueOf(totalTokens * 0.00009).setScale(5, RoundingMode.HALF_UP);
    log.info("=====================金额统计=====================");
    log.info("当前使用token总数为" + totalTokens + "总耗费金额为:" + money);
    // log.info("=====================当前聊天记录=====================");
    // for (Message o : req.getMessages()) {
    // log.info("" + o);
    // }
    log.info("=====================回答内容输出完毕=====================");
  }

  private String getAccessToken() throws IOException {
    // 检查token是否过期
    long currentTime = System.currentTimeMillis();
    if (accessToken != null && currentTime < tokenExpireTime) {
      return accessToken;
    }

    // token过期或未初始化，重新获取
    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
    RequestBody body = RequestBody.create(mediaType,
        "grant_type=client_credentials&client_id=" + akey + "&client_secret=" + skey);
    Request request = new Request.Builder()
        .url("https://aip.baidubce.com/oauth/2.0/token")
        .method("POST", body)
        .addHeader("Content-Type", "application/x-www-form-urlencoded")
        .build();
    Response response = httpClient.newCall(request).execute();
    String responseStr = new String(response.body().bytes(), "UTF-8");
    JSONObject jsonResponse = JSONObject.parseObject(responseStr);

    // 获取token和过期时间
    accessToken = jsonResponse.getString("access_token");
    // token有效期通常为30天，这里设置为29天以确保安全
    tokenExpireTime = currentTime + 29 * 24 * 60 * 60 * 1000L;

    return accessToken;
  }

  // produces 属性用于指定该方法返回的内容类型（MIME 类型）
  // 。produces = "text/event-stream" 表示该方法将返回一种特定格式的响应。
  // @PostMapping(produces = "text/event-stream")
  // public SseEmitter
  // handleStream(@org.springframework.web.bind.annotation.RequestBody String
  // requestBody, HttpSession session) throws IOException {
  // @GetMapping(value = "/test", produces = "text/event-stream")
  // public SseEmitter handleStream() {
  // SseEmitter emitter = new SseEmitter();
  //
  // new Thread(() -> {
  // try {
  // // 发送重连时间
  // emitter.send("retry: 5000\n\n"); // 5秒重连时间
  //
  // for (int i = 0; i < 20; i++) {
  // emitter.send("data: " + i + "\n\n"); // 逐条发送消息
  // Thread.sleep(1000); // 每秒发送一次
  // }
  // emitter.complete(); // 完成事件
  // } catch (IOException | InterruptedException e) {
  // emitter.completeWithError(e); // 发送错误
  // }
  // }).start();
  //
  // return emitter; // 返回 SseEmitter
  // }
}
