package com.ruoyi.web.controller.quiz.admin;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.AjaxResult;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/quiz/question/ai-image")
public class QuestionImageAiProxyController {

    @Value("${daming.ai.base-url:http://localhost:8090}")
    private String aiBaseUrl;

    @Value("${daming.ai.timeout-seconds:120}")
    private int timeoutSeconds;

    @Value("${daming.ai.auth-token:}")
    private String aiAuthToken;

    private OkHttpClient buildHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 创建图片录题任务（透传到 daming-ai）
     */
    @PreAuthorize("@ss.hasPermi('quiz:question:add')")
    @PostMapping("/task")
    public AjaxResult createTask(@RequestPart("imageFile") MultipartFile imageFile,
                                 @RequestParam(value = "autoAssignColumn", required = false, defaultValue = "false") Boolean autoAssignColumn,
                                 @RequestParam(value = "pointCandidates", required = false) String pointCandidates,
                                 @RequestParam(value = "isRealQuestion", required = false, defaultValue = "false") Boolean isRealQuestion) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            return AjaxResult.error("图片不能为空");
        }
        String fileName = imageFile.getOriginalFilename() == null ? "question-image.png" : imageFile.getOriginalFilename();
        String contentType = imageFile.getContentType() == null ? "application/octet-stream" : imageFile.getContentType();

        RequestBody fileBody = RequestBody.create(MediaType.parse(contentType), imageFile.getBytes());
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("imageFile", fileName, fileBody)
                .addFormDataPart("autoAssignColumn", String.valueOf(Boolean.TRUE.equals(autoAssignColumn)))
                .addFormDataPart("isRealQuestion", String.valueOf(Boolean.TRUE.equals(isRealQuestion)));
        if (pointCandidates != null && !pointCandidates.trim().isEmpty()) {
            multipartBuilder.addFormDataPart("pointCandidates", pointCandidates);
        }

        Request.Builder requestBuilder = new Request.Builder()
                .url(trimRightSlash(aiBaseUrl) + "/ai/question-image/task")
                .post(multipartBuilder.build());
        if (aiAuthToken != null && !aiAuthToken.trim().isEmpty()) {
            requestBuilder.addHeader("X-AI-Token", aiAuthToken.trim());
        }
        Request request = requestBuilder.build();

        try (Response response = buildHttpClient().newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            String body = responseBody == null ? "" : responseBody.string();
            if (!response.isSuccessful()) {
                return AjaxResult.error("调用daming-ai创建任务失败，status=" + response.code() + ", body=" + body);
            }
            return AjaxResult.success(parseJson(body));
        }
    }

    /**
     * 查询图片录题任务（透传到 daming-ai）
     */
    @PreAuthorize("@ss.hasPermi('quiz:question:query')")
    @GetMapping("/task/{taskId}")
    public AjaxResult queryTask(@PathVariable("taskId") String taskId) throws IOException {
        Request.Builder requestBuilder = new Request.Builder()
                .url(trimRightSlash(aiBaseUrl) + "/ai/question-image/task/" + taskId)
                .get();
        if (aiAuthToken != null && !aiAuthToken.trim().isEmpty()) {
            requestBuilder.addHeader("X-AI-Token", aiAuthToken.trim());
        }
        Request request = requestBuilder.build();
        try (Response response = buildHttpClient().newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            String body = responseBody == null ? "" : responseBody.string();
            if (response.code() == 404) {
                return AjaxResult.error("任务不存在: " + taskId);
            }
            if (!response.isSuccessful()) {
                return AjaxResult.error("调用daming-ai查询任务失败，status=" + response.code() + ", body=" + body);
            }
            return AjaxResult.success(parseJson(body));
        }
    }

    private String trimRightSlash(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        if (text.endsWith("/")) {
            return text.substring(0, text.length() - 1);
        }
        return text;
    }

    private Object parseJson(String body) {
        try {
            return JSON.parse(body);
        } catch (Exception e) {
            JSONObject obj = new JSONObject();
            obj.put("raw", body);
            obj.put("parseError", e.getMessage());
            return obj;
        }
    }
}

