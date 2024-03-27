package com.demo.xihu.controller;
import okhttp3.*;

import java.io.*;

import com.alibaba.fastjson.JSON;
import com.demo.xihu.dto.GuestListDTO;
import com.demo.xihu.result.Result;
import com.demo.xihu.utils.IdentityUrlUtil;
import com.demo.xihu.vo.GuestShowVO;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.demo.xihu.utils.IdentityUrlUtil.getAuthUrl;

@RestController
@RequestMapping("dev-api/poster")
@Slf4j
@Tag(name = "生成海报相关接口", description = "无")
public class PosterController {

    public static final String hostUrl = "https://spark-api.cn-huabei-1.xf-yun.com/v2.1/tti";
    public static final String appid = "452f8576";
    public static final String apiSecret = "NmYyYjhiMDZmOGIxOGI5NzlmZjRlNWNl";
    public static final String apiKey = "6851072f04247a83d15693e84d309a96";


    @PostMapping()
    @Operation(summary = "生成图片")
    public Result GetImage(@RequestBody Map<String, String> params) throws Exception {
        String text = params.get("text");
        System.err.println(text);
        String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
        String json = "{\n" +
                "  \"header\": {\n" +
                "    \"app_id\": \"" + appid + "\",\n" +
                "    \"uid\": \"" + UUID.randomUUID().toString().substring(0, 15) + "\"\n" +
                "  },\n" +
                "  \"parameter\": {\n" +
                "    \"chat\": {\n" +
                "      \"domain\": \"s291394db\",\n" +
                "      \"temperature\": 0.5,\n" +
                "      \"max_tokens\": 4096,\n" +
                "      \"width\": 720,\n" +
                "      \"height\": 1280\n" +
                "    }\n" +
                "  },\n" +
                "  \"payload\": {\n" +
                "    \"message\": {\n" +
                "      \"text\": [\n" +
                "        {\n" +
                "          \"role\": \"user\",\n" +
                "          \"content\": \"" + text + "\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  }\n" +
                "}";
        // 发起Post请求
        // System.err.println(json);
        String res = IdentityUrlUtil.doPostJson(authUrl, null, json);
        return Result.success("查询成功", JSON.parse(res));
    }

}
