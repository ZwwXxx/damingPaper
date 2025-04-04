package com.dm.quiz.stream;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.dm.quiz.domain.ai.Message;
import com.dm.quiz.domain.ai.RequestMessage;
import com.sun.net.httpserver.*;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class StreamHandler implements HttpHandler {

    @Value("${wenxin.akey}")
    private String akey;

    @Value("${wenxin.skey}")
    private String skey;

    RequestMessage requestMessage = new RequestMessage();

    /**
     * Handle the given request and generate an appropriate response.
     * See {@link HttpExchange} for a description of the steps
     * involved in handling an exchange.
     *
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws NullPointerException if exchange is <code>null</code>
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // 设置任何域名允许跨域访问，前端请求被通过，并设置200状态码发送回去 标识服务器成功的接收到请求
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, 0);
        // 获取请求的请求参数
        // http交换中的请求体转为字符输入流
        InputStreamReader inputStreamReader = new InputStreamReader(exchange.getRequestBody());
        // 字符流升级为换层字符流，提高性能，丰富方法(readLine)
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        // 字符串构建器内部维护了一个可变数组，拼接不开辟新的string对象
        // 如果使用res+=xxx这种拼接方式，每次拼接成功一次后会产生一个全新的对象
        // 减少内存消耗，且提供append方法，可读性强
        StringBuilder stringBuilder = new StringBuilder();
        // 从构建的流里循环读取line 追加到builder里
        // 需要先设置一个line用来接收每次循环得到的readline
        String line;
        // 只要读出来不为空就循环读取
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        // 关闭流，否则内存泄漏，也就是一直屯着 服务器变卡
        bufferedReader.close();
        // 字符串构造器转字符串提取为json对象，键值获取前端传来提问内容
        String content = (String) JSON.parseObject(stringBuilder.toString()).get("content");

        // 设置发言人进入聊天记录数组
        Message user = new Message("user", content);
        requestMessage.addMessage(user);


        // 将该请求转json作为请求体发送调用ai Api
        String jsonString = JSON.toJSONString(requestMessage);

        // 发起流式请求，在requestMessage中的stream设置为true，后续收到的响应内容则为流式的多段data
        // 借助okHttp请求客户端
        OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();
        // 指定请求体类型,创建对api的请求体，内容为聊天记录和请求体类型
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(mediaType, jsonString);
        // 创建请求，调用大模型api
        String baseUrl = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/ernie_speed";
        Request request = new Request.Builder()
                .url(baseUrl + "?access_token=" + getAccessToken())
                .method("POST", requestBody)
                .addHeader("Content-Type", "application/json")
                .build();
        // 通过okHttp远程调用客户端发送该请求,或得的响应体由大模型api流式给我们传输
        Response response = HTTP_CLIENT.newCall(request).execute();

        // 大模型流给我们，我们流给下游前端
        OutputStream outputStreamForFront = exchange.getResponseBody();
        // 在此之前，初始化一些值
        StringBuilder result = new StringBuilder();
        Integer totalTokens = 0;

        // ai接口响应成功
        if (response.isSuccessful()) {
            // 因为响应体内容为字节格式，转响应体为字节输入流，然后读到字节数组，通过String对象转为字符串
            ResponseBody body = response.body();
            InputStream inputStream = body.byteStream();
            // 中转作用，给while循环读取记录读取数量然后最后将字节数组转化为String对象
            byte[] buffer = new byte[1024];
            // 记录读了多少，读多少就循环给到String多少
            int size = 0;
            // 不为负一说明该流的内容还没读完，读完就下一个
            while ((size = inputStream.read(buffer)) != -1) {
                // 第一个参数为字节提供者
                String data = new String(buffer, 0, size);
                // 由于一个流可能同时有好几个data： 。通过\n\n分隔开，我们转为字符串数组
                String[] lines = data.split("\n");
                for (String li : lines) {
                    // 当前数据合并完毕，下一条打头为data：时，进行新数据的入列
                    // 队列下一个的消费对象，检测到，则将当前消费对象输出，填充新的消费对象
                    if (li.startsWith("data: ")) {
                        // 等于这种情况只会出现1次，就是一开始队列为空时，
                        // 根据 isEnd 确定要使用的数据
                        String dataToWrite = li.substring(6);
                        // 写入请求的输出流 到前端
                        outputStreamForFront.write(dataToWrite.getBytes());
                        outputStreamForFront.flush();
                        JSONObject jsonObject = JSON.parseObject(dataToWrite);
                        // 记录总token使用数量
                        System.out.println("=====================机器人逐步回复内容=====================");
                        String newMessage = (String) jsonObject.get("result");
                        // 依次打印新内容
                        System.out.println("信息为：" + newMessage);
                        totalTokens = (Integer) jsonObject.getJSONObject("usage").getInteger("total_tokens");
                        result.append(newMessage);
                        // 刷新时客户端收到信息
                    }
                    // 将队列里的信息进行消费
                    // rawData = li;
                    // } else{
                    //     // 若检测不到，为空行，则合并为最终的data：
                    //     rawData = rawData + li;
                    // }
                }
            }
            // 将上下文加入记录
            // 循环结束，日志录入
            BigDecimal money = BigDecimal.valueOf(totalTokens * 0.00009);
            // 设置小数位数为两位，使用四舍五入
            money = money.setScale(5, RoundingMode.HALF_UP);
            System.out.println("=====================金额统计=====================");
            System.out.println("当前使用token总数为" + totalTokens + "总耗费金额为:" + money);
            Message message = new Message("assistant", result + "\n");
            requestMessage.addMessage(message);
            System.out.println("=====================当前聊天记录=====================");
            requestMessage.pringMessages();
            System.out.println("=====================回答内容输出完毕=====================");
        }

    }
    public String getAccessToken() throws IOException {
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
}
