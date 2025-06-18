package com.wm.ai.controller;

import com.wm.ai.service.impl.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "ai-chat")
@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;


    @Operation(summary = "商品推荐对话智能体-sse")
    @PostMapping(value = "/chat")
    public Flux<ServerSentEvent<String>> chat(@RequestBody ChatRequest request) {

        return chatService.chat(request);
    }

    @Operation(summary = "商品推荐对话智能体-mono")
    @PostMapping(value = "/chat_mono")
    public Mono<String> chat_mono(@RequestBody ChatRequest request) {

        return chatService.chat_mono(request);
    }

    public record ChatRequest(String sessionId, String userInput) {
    }
}