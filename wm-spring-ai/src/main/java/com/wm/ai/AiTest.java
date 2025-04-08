package com.wm.ai;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;

public class AiTest {
    public static void main(String[] args) {
        //ds();
        qw();
    }

    private static void qw() {
        String apiKey = System.getenv("DASHSCOPE_APIKEY");
        HttpRequest request = HttpUtil.createPost("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions");
        request.header("Authorization", "Bearer "+apiKey+"");
        request.header("Content-Type", "application/json");
        request.body("{\n  \"model\": \"qwen-plus\",\n  \"messages\": [\n    {\n      \"role\": \"user\",\n      \"content\": \"你是谁？\"\n    }\n  ],\n  \"stream\": false,\n  \"max_tokens\": 512,\n  \"stop\": [\n    \"null\"\n  ],\n  \"temperature\": 0.7,\n  \"top_p\": 0.7,\n  \"top_k\": 50,\n  \"frequency_penalty\": 0.5,\n  \"n\": 1,\n  \"response_format\": {\n    \"type\": \"text\"\n  },\n  \"tools\": [\n    {\n      \"type\": \"function\",\n      \"function\": {\n        \"description\": \"<string>\",\n        \"name\": \"<string>\",\n        \"parameters\": {},\n        \"strict\": false\n      }\n    }\n  ]\n}");
        HttpResponse response = request.execute();
        System.out.println(response.toString());
    }

    private static void ds() {
        String apiKey = System.getenv("SILICONFLOW_DS_APIKEY");
        HttpRequest request = HttpUtil.createPost("https://api.siliconflow.cn/v1/chat/completions");
        request.header("Authorization", "Bearer "+apiKey+"");
        request.header("Content-Type", "application/json");
        request.body("{\n  \"model\": \"deepseek-ai/DeepSeek-V3\",\n  \"messages\": [\n    {\n      \"role\": \"user\",\n      \"content\": \"你是谁？\"\n    }\n  ],\n  \"stream\": false,\n  \"max_tokens\": 512,\n  \"stop\": [\n    \"null\"\n  ],\n  \"temperature\": 0.7,\n  \"top_p\": 0.7,\n  \"top_k\": 50,\n  \"frequency_penalty\": 0.5,\n  \"n\": 1,\n  \"response_format\": {\n    \"type\": \"text\"\n  },\n  \"tools\": [\n    {\n      \"type\": \"function\",\n      \"function\": {\n        \"description\": \"<string>\",\n        \"name\": \"<string>\",\n        \"parameters\": {},\n        \"strict\": false\n      }\n    }\n  ]\n}");
        HttpResponse response = request.execute();
        System.out.println(response.toString());
    }
}
