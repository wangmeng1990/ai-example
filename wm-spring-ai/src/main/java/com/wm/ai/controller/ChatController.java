package com.wm.ai.controller;

import com.wm.ai.service.impl.ChatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "ai-chat")
@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @GetMapping("/chat1")
    public String cha1(@RequestParam String userInput) {
        return chatService.chat1(userInput);
    }

    @PostMapping(value = "/chat2")
    public Mono<String> chat2(@RequestBody ChatRequest request ) {
        return chatService.chat2(request);
    }

    //基于上下文
    @PostMapping(value = "/chat3", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chat3(@RequestBody ChatRequest request) {
        return chatService.chat3(request);
    }

    @PostMapping(value = "/chat4", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chat4(@RequestBody ChatRequest request) {
        return chatService.chat4(request);
    }

    /**
     * 返回自定义实体
     * @param request
     * @return
     */
    @PostMapping(value = "/chat5")
    public MyChatResponse chat5(@RequestBody ChatRequest request ) {
        return chatService.chat5(request);
    }

    /**
     * QuestionAnswerAdvisor
     * @param request
     * @return
     */
    @PostMapping(value = "/chat6")
    public Mono<String> chat6(@RequestBody ChatRequest request ) {
        return chatService.chat6(request);
    }


    /**
     * VectorStoreChatMemoryAdvisor
     * @param request
     * @return
     */
    @PostMapping(value = "/chat7")
    public Mono<String> chat7(@RequestBody ChatRequest request) {
        return chatService.chat7(request);
    }



    /**
     * functionCall
     * @param request
     * @return
     */
    @PostMapping(value = "/chat9")
    public Mono<String> chat9(@RequestBody ChatRequest request ) {
        return chatService.chat9(request);
    }

    /**
     * 聊天会话管理
     * 对话和对话保存
     * @param request
     * @return
     */
    @PostMapping(value = "/chat10")
    public Mono<String> chat10(@RequestBody ChatRequest request) {
        return chatService.chat10(request);
    }

    /**
     * 根据上传文件对话
     * @return
     */
    @PostMapping(value = "/chat11")
    public Mono<String> chat11(@RequestParam(name = "sessionId") String sessionId,@RequestParam(name = "userInput") String userInput,@RequestParam(name = "file",required = false) MultipartFile file) {
        return chatService.chat11(sessionId, userInput, file);
    }

    /**
     * 重排序
     * @param request
     * @return
     */
    @PostMapping(value = "/chat12")
    public Mono<String> chat12(@RequestBody ChatRequest request ) {
        return chatService.chat12(request);
    }

    /**
     * RetrievalAugmentationAdvisor:整合相似度检索，文档检索,提示词增强等
     * @param request
     * @return
     */
    @PostMapping(value = "/chat13")
    public Mono<String> chat13(@RequestBody ChatRequest request ) {
        return chatService.chat13(request);
    }

    /**
     * MCP
     * @param request
     * @return
     */
    @PostMapping(value = "/chat14")
    public Mono<String> chat14(@RequestBody ChatRequest request ) {
        return chatService.chat14(request);
    }

    /**
     * 结构化输出-entity
     * @param request
     * @return
     */
    @PostMapping(value = "/chat15")
    public Mono<String> chat15(@RequestBody ChatRequest request ) {
        return chatService.chat15(request);
    }

    public record ChatRequest(String sessionId, String userInput, String followMessage) {
    }

    public record MyChatResponse(String id, String msg) {
    }
}