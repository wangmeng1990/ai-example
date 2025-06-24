package com.wm.ai.service.impl;
import com.wm.ai.agent.GoodAgent;
import com.wm.ai.controller.ChatController;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Data
public class ChatService {

    private final GoodAgent goodAgent;

    /**
     * 商品推荐chat
     */
    public Flux<ServerSentEvent<String>> chat(ChatController.ChatRequest request) {

        return goodAgent.doChat(request.userInput());
    }

    public Mono<String> chat_mono(ChatController.ChatRequest request) {

        return goodAgent.doChat_mono(request.userInput());
    }

    public SseEmitter chat_sseemiter(ChatController.ChatRequest request) {

        return goodAgent.doChat_sseemiter(request.userInput());
    }
}