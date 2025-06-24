package com.wm.ai.agent;

import com.alibaba.cloud.ai.advisor.RetrievalRerankAdvisor;
import com.wm.ai.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GoodAgent  {

    private ChatClient chatClient;


    public GoodAgent(ToolCallbackProvider toolCallbackProvider,
                     ChatModel dashScopeChatModel,
                     RagComponent ragComponent) {

        String SYSTEM_PROMPT="""
                你是一个智能商品推荐助手，需按以下逻辑流程完成用户服务
                
                一、核心职能定义
                角色定位：根据用户咨询分析购买意图，调用工具获取商品信息并匹配推荐
                工具使用：可通过接口查询商品信息，查询条件有商品名称 / 商品分类，查询条件非必填
                你需要根据用户提问分析可能涉及的商品名称和商品分类，如果无法分析出商品名称和商品分类可以忽略
                商品分类包括：[服饰鞋包、数码、化妆品]
                例子：用户提问【推荐一款口红】 需要提取的商品名称 / 商品分类为：口红/化妆品
                你推荐的商品必须只能从good_list内容中获取的,不可杜撰商品
                
                二、信息识别与处理规则
                商品信息提取
                优先识别用户需求中的商品名称（如 白色运动鞋）、商品分类（如 服饰鞋包）
                若信息缺失（例：用户仅说 想买礼物），需礼貌追问：请问您想了解哪类商品呢？比如服饰鞋包、化妆品等
                用户画像应用
                若已获取用户画像（如年龄、偏好标签），需结合以下维度精准匹配：
                消费层级（例：为 高端用户 优先推荐 高端大气上档次 分类商品）
                历史偏好（例：曾购买口红用户优先推荐同品牌新品）
                
                三、推荐话术规范
                话术原则：
                基于库存商品真实描述润色（例：这款粉底液含养肤成分，持妆 12 小时不脱妆）
                禁止行为：虚假宣传（如 绝对全网最低价）、夸大功效（如 一用即白）
                无匹配商品处理：
                需主动挖掘需求：目前没有找到合适商品，能否告诉我您对商品的具体要求呢？
                
                四、问题过滤机制
                拒绝回答范围：
                非商品咨询类问题（如 今天天气如何）
                敏感问题（政治、暴力、色情等）
                响应规范：
                统一回复：抱歉，我目前仅支持商品咨询相关问题哦～
                """;

        // 初始化客户端
        MessageChatMemoryAdvisor messageChatMemoryAdvisor =ragComponent.buildMessageChatMemoryAdvisor();
        //（效果不是很好）过多的处理反而产生干扰
        //RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = ragComponent.buildRetrievalAugmentationAdvisor();

        QuestionAnswerAdvisor questionAnswerAdvisor = ragComponent.buildQuestionAnswerAdvisor();
        RetrievalRerankAdvisor retrievalRerankAdvisor = ragComponent.buildRetrievalRerankAdvisor();

        this.chatClient= ChatClient.builder(dashScopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(new MyLoggerAdvisor(), messageChatMemoryAdvisor,questionAnswerAdvisor,retrievalRerankAdvisor)
                //.defaultAdvisors(new SimpleLoggerAdvisor(), messageChatMemoryAdvisor)
                .defaultToolCallbacks(toolCallbackProvider)
                //spring AI 1.0.0 要求：工具方法有toolContext参数则必须需要传入toolContext
                .defaultToolContext(Map.of("userId", "1"))
                .build();
    }

    public Flux<ServerSentEvent<String>> doChat(String userInput) {

        return doChat_stream(userInput)
                .map(content -> ServerSentEvent.builder(content).event("answer").build())
                //问题回答结速标识,以便前端消息展示处理
                .concatWithValues(ServerSentEvent.builder("[DONE]").event("finish").build())
                .onErrorResume(e -> Flux.just(ServerSentEvent.builder("Error: " + e.getMessage()).event("error").build()));
    }

    public Mono<String> doChat_mono(String userInput) {

        return doChat_stream(userInput).collect(Collectors.joining())
                //问题回答结速标识,以便前端消息展示处理
                .onErrorResume(e -> {
                    System.err.println("Error occurred during chat: " + e.getMessage());
                    return Mono.just("Error: " + e.getMessage());
                });
    }

    private Flux<String> doChat_stream(String userInput) {
        return chatClient.prompt()
                .user(userInput)
                .stream()
                .content();
    }

    public SseEmitter doChat_sseemiter(String userInput) {
        SseEmitter sseEmitter = new SseEmitter(180000L);
        doChat_stream(userInput)
                .subscribe(content -> {
                    try {
                        sseEmitter.send(content);
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                    }
                }, sseEmitter::completeWithError, sseEmitter::complete);
        return sseEmitter;
    }
}






