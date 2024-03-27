package com.demo.xihu.controller;

import com.alibaba.fastjson.JSON;
import com.demo.xihu.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
@RestController
@Tag(name = "生成海报相关接口", description = "无")
@RequestMapping("dev-api/poster/baiduNB")
@Slf4j
@Tag(name = "生成海报相关接口", description = "无")
public class PosterBaiduNBController {

        public static final String API_KEY = "vPO6t6v3vWtAnsjrFoFx9PqO";
        public static final String SECRET_KEY = "ViwIxClh9LZjP5hUEwHgSwbBHx1ng5yv";

        static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder()
                .connectTimeout(600, TimeUnit.SECONDS) // 设置连接超时时间
                .readTimeout(600, TimeUnit.SECONDS)    // 设置读取超时时间
                .build();

        @PostMapping()
        @Operation(summary = "生成图片")
        public Result GetImage(@org.springframework.web.bind.annotation.RequestBody Map<String, String> params) throws IOException, InterruptedException {
            String text = params.get("text");

            // 发送文本生成图片的请求，获取taskId
            String taskId = submitText(text);

            // 定义轮询间隔（毫秒）
            long pollInterval = 6000; // 6秒
            long maxPollAttempts = 10; // 最大轮询次数
            long pollAttempts = 0;
            String responseData = null;

            // 轮询获取结果
            while (pollAttempts < maxPollAttempts) {
                // 递增轮询计数器
                pollAttempts++;

                try {
                    // 构建请求体 JSON
                    JSONObject requestBodyJson = new JSONObject();
                    requestBodyJson.put("task_id", taskId);

                    // 发送请求
                    MediaType mediaType = MediaType.parse("application/json");
                    RequestBody body = RequestBody.create(mediaType, requestBodyJson.toString());
                    Request request = new Request.Builder()
                            .url("https://aip.baidubce.com/rpc/2.0/ernievilg/v1/getImgv2?access_token="  + getAccessToken())
                            .method("POST", body)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Accept", "application/json")
                            .build();

                    try (Response response = HTTP_CLIENT.newCall(request).execute()) {
                        if (!response.isSuccessful()) {
                            // 请求失败，打印错误信息，但继续轮询
                            log.error("Unexpected response code: {}", response);
                        } else {
                            responseData = response.body().string();
                            System.out.println("查询结果的response: " + responseData);

                            // 解析 JSON 获取状态
                            JSONObject jsonResponse = new JSONObject(responseData);
                            JSONObject data = jsonResponse.getJSONObject("data");
                            Integer status = data.getInt("task_progress");

                            if (status == 1) {
                                // 任务完成，返回结果
                                return Result.success(JSON.parse(responseData));
                            }
                        }
                    } catch (IOException e) {
                        // 发生异常，打印错误信息，但继续轮询
                        log.error("Error occurred during request: {}", e.getMessage());
                    }

                    // 任务未完成，等待一段时间后继续轮询
                    Thread.sleep(pollInterval);
                } catch (Exception ex) {
                    // 发生异常，打印错误信息，但继续轮询
                    log.error("An error occurred during polling: {}", ex.getMessage());
                }
            }

            // 达到最大轮询次数仍未完成，返回错误
            return Result.error("任务处理超时，请稍后重试");
        }


        /**
         * 工具类，
         * @return
         * @throws IOException
         */
        private String submitText(String text) throws IOException {

            // 构建请求体 JSON
            JSONObject requestBodyJson = new JSONObject();
            requestBodyJson.put("prompt", text);
            requestBodyJson.put("width", 1024);
            requestBodyJson.put("height", 1024);
            requestBodyJson.put("image", "");//（base64 编码）
            // 发送请求
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType,requestBodyJson.toString());
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/rpc/2.0/ernievilg/v1/txt2imgv2?access_token=" + getAccessToken())
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();
            Response response = HTTP_CLIENT.newCall(request).execute();
            String responseData = response.body().string();
            System.out.println("提交请求的response"+responseData);

            // 解析 JSON
            JSONObject jsonResponse = new JSONObject(responseData);
            JSONObject data = jsonResponse.getJSONObject("data");
            String taskId = data.getString("task_id");

            return taskId;
        }
        /**
         * 从用户的AK，SK生成鉴权签名（Access Token）
         *
         * @return 鉴权签名（Access Token）
         * @throws IOException IO异常
         */
        static String getAccessToken() throws IOException {
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                    + "&client_secret=" + SECRET_KEY);
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/oauth/2.0/token")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            Response response = HTTP_CLIENT.newCall(request).execute();
            return new JSONObject(response.body().string()).getString("access_token");
        }

}
