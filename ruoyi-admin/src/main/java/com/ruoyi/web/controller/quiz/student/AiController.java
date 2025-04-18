package com.ruoyi.web.controller.quiz.student;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.dm.quiz.domain.ai.Message;
import com.dm.quiz.domain.ai.RequestMessage;
import com.ruoyi.common.core.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;

import okhttp3.*;
import okhttp3.RequestBody;
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
    // 由于单纯的sse事件无法使用post请求，这里我们使用post接收数据，和sse接收get建立连接的组合方式
    // 使用并发map，每次新增事件对象会拷贝一份新的，副本与原始用户正在读取的进行了隔离
    // 如果使用arraylist，会在遍历获取事件对象的时候，恰巧b用户新增事件窗口，产生concurrentmodification并发修改错误
    // 此实现类适合读多写少的，写因为牵扯到拷贝新内存，内存占用大，读的话就防止并发，多线程读取安全
    // 使用userId进行区分消息发送对象
    // private final ConcurrentHashMap<String, SseEmitter> emitterListMap = new ConcurrentHashMap<>();

    // 且该map为服务器共享的全局事件池
    @DeleteMapping("/clear-context")
    public AjaxResult clearContext(HttpSession session) {
        // 针对当前session Id的 record记录
        RequestMessage req = (RequestMessage) session.getAttribute("req");
        log.info("要清理的当前线程{}", req);
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
            return AjaxResult.success("流已停止");
        } else {
            return AjaxResult.error("没有正在运行的流");
        }
    }


    @GetMapping(value = "/openSseRaw", produces = "text/event-stream;charset=UTF-8")
    public SseEmitter handleStreamX(String content, HttpSession session) throws IOException {
        currentEmitter= new SseEmitter();
        RequestMessage req = (RequestMessage) session.getAttribute("req");
        if (req == null) {
            System.out.println("session记录为空");
            req = new RequestMessage();
        }
        Message user = new Message("user", content + "\n");
        req.addMessage(user);

        String jsonString = JSON.toJSONString(req);

        // 创建 HTTP 客户端
        OkHttpClient httpClient = new OkHttpClient();

        // 设置请求体和请求
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(mediaType, jsonString);
        String baseUrl = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/ernie_speed";
        Request request = new Request.Builder()
                .url(baseUrl + "?access_token=" + getAccessToken())
                .method("POST", requestBody)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = httpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            // 使用新线程处理读取和发送，确保流在该线程中可用
            // lambda表达式需要变量的不变性，防止出现修改的并发问题，这里用final变量引用req
            isCompleted = false;
            final RequestMessage finalReq = req;
            // 执行完所有消息推送就会关闭线程
            new Thread(() -> {
                log.info("新线程的开启");
                // try-with-resources 语句会在 try 块结束时自动关闭所有在括号内声明的资源。
                // 这对于 InputStream 和 BufferedReader 是非常有用的，因为它们实现了 AutoCloseable 接口。
                // 当你在括号内声明多个资源时，可以使用逗号分隔它们。例如：
                // try (InputStream inputStream = new FileInputStream("file.txt"  );
                //      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                //     // 读取内容
                // }
                // 这种方式确保在 try 块结束时，所有资源都会被正确关闭，避免内存泄漏和资源占用。
                Integer totalTokens = 0;
                StringBuilder result = new StringBuilder();
                try (
                        // 这一步有可能会超时导致线程终结提前跳出循环，因此得设置一个超时时间
                        // 一旦超时，后续数据乱发，然后输出时是一堆乱七八糟的中文
                        // 且超时后线程会退出，但是后续有新响应又会尝试调用该send发送消息方法
                        // 不过此时已经complete了，因此会报错ResponseEmitter has already completed
                        // 在配置文件里给异步请求设置超时时间为长一点的即可
                        // 或者通过标志位或其他控制逻辑，确保在发送消息之前先检查线程的状态。
                        InputStream inputStream = response.body().byteStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null && !isCompleted) {
                        if (line.startsWith("data: ")  ) {
                            String dataToWrite = line.substring(6).trim(); // 去掉前缀和空格
                            System.out.println("新消息"+dataToWrite);
                            currentEmitter.send(dataToWrite + "\n\n"); // 逐条发送消息
                            JSONObject jsonObject = JSON.parseObject(dataToWrite);
                            totalTokens = jsonObject.getJSONObject("usage").getInteger("total_tokens");
                            String buffer = (String) jsonObject.get("result");
                            result.append(buffer);
                            boolean isEnd = (boolean) jsonObject.get("is_end");
                            if (isEnd) {
                                break;
                            }
                        }
                    }
                } catch (IOException e) {
                    // 发生 IO 异常时，完成 emitter 并发送错误
                    currentEmitter.completeWithError(e);
                } finally {
                    log.info("线程结束");
                    isCompleted = true;
                    // finalReq，作为锁，防止多个请求并发 覆盖掉之前的内容
                    synchronized (finalReq) {
                        recordInfo(totalTokens, String.valueOf(result), finalReq, session);
                    }
                    System.out.println("=========================");
                    System.out.println("正在关闭complete Emitter");
                    currentEmitter.complete(); // 确保在完成后关闭连接
                    System.out.println("=========================");
                }
            }).start();
        } else {
            log.error("流式请求异常: " + response);
            currentEmitter.completeWithError(new RuntimeException("请求失败"));
        }


        return currentEmitter; // 返回 SseEmitter
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
        //     log.info("" + o);
        // }
        log.info("=====================回答内容输出完毕=====================");
    }

    private String getAccessToken() throws IOException {
        OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + akey + "&client_secret=" + skey);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return JSONObject.parseObject(response.body().string()).getString("access_token");
    }


    // produces 属性用于指定该方法返回的内容类型（MIME 类型）
    // 。produces = "text/event-stream" 表示该方法将返回一种特定格式的响应。
    // @PostMapping(produces = "text/event-stream")
    // public SseEmitter handleStream(@org.springframework.web.bind.annotation.RequestBody String requestBody, HttpSession session) throws IOException {
    // @GetMapping(value = "/test", produces = "text/event-stream")
    // public SseEmitter handleStream() {
    //     SseEmitter emitter = new SseEmitter();
    //
    //     new Thread(() -> {
    //         try {
    //             // 发送重连时间
    //             emitter.send("retry: 5000\n\n"); // 5秒重连时间
    //
    //             for (int i = 0; i < 20; i++) {
    //                 emitter.send("data: " + i + "\n\n"); // 逐条发送消息
    //                 Thread.sleep(1000); // 每秒发送一次
    //             }
    //             emitter.complete(); // 完成事件
    //         } catch (IOException | InterruptedException e) {
    //             emitter.completeWithError(e); // 发送错误
    //         }
    //     }).start();
    //
    //     return emitter; // 返回 SseEmitter
    // }
}
