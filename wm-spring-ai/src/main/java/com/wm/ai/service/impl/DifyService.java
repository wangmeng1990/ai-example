package com.wm.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class DifyService {

    public String chat(String userInput) {
        return doChat(userInput);
    }

    private String doChat(String userInput) {
        StringBuilder response = new StringBuilder();
        try {
            String apiKey = "app-nvzeIsIewjMK5UoX5OUCC66c";
            String apiUrl = "https://api.dify.ai/v1/chat-messages";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 请求配置
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // 构造 JSON 请求体
            String jsonBody = String.format("""
                                {
                                  "inputs": {},
                                  "query": "%s",
                                  "response_mode": "streaming",
                                  "conversation_id": "",
                                  "user": "abc-123"
                                }
                                """, userInput);

            // 写入请求体
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 打印响应
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            InputStream is = (responseCode < 400) ? conn.getInputStream() : conn.getErrorStream();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    JSONObject jsonObject = JSONObject.parseObject(responseLine.trim().replaceFirst("data:",""));
                    if  (null!=jsonObject) {

                        String answer = jsonObject.getString("answer");
                        if (StrUtil.isNotEmpty(answer)){
                            String decodedAnswer = java.util.regex.Pattern
                                .compile("\\\\u([0-9a-fA-F]{4})")
                                .matcher(answer)
                                .replaceAll(match -> Character.toString((char) Integer.parseInt(match.group(1), 16)));
                            response.append(decodedAnswer);
                        }
                    }
                }
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.toString();
    }
}
