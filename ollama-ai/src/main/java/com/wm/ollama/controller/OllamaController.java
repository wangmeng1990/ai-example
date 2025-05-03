package com.wm.ollama.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ai-ollama")
@RestController
@RequestMapping("/ollama")
public class OllamaController {

    @Autowired
    private ChatModel ollamaChatModel;

    @Operation(summary = "ollama本地部署模型chat")
    @GetMapping("/chat1")
    public String chat1(@RequestParam String userInput) {
        ChatClient chatClient = ChatClient.builder(ollamaChatModel).build();
        String content = chatClient.prompt()
            .user(userInput)
            .advisors(new SimpleLoggerAdvisor())
            .call()
            .content();
        return content;
    }
}
