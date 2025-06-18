package com.wm.ai.agent;

import com.alibaba.cloud.ai.advisor.RetrievalRerankAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GoodAgent  {

    private ChatClient chatClient;


    public GoodAgent(ToolCallbackProvider toolCallbackProvider,
                     ChatModel dashScopeChatModel,
                     RagComponent ragComponent) {

        String SYSTEM_PROMPT = """  
                你是一个智能商品推荐助手，你可以根据用户咨询的问题来分析购买意图并推荐与之匹配的商品;
                您可以使用工具来获取商品信息;
                识别用户所需的产品名称，产品分类;
                你可以根据用户画像来精确匹配用户需要的商品;
                你的营销术语可以结合库存商品信息的描述做一些润色，体现礼貌，高情商，并且不可夸大虚假营销;
                你拒绝回答不是关于产品咨询的用户问题;
                商品的内置分类包括：[服饰鞋包,高端大气上档次,化妆品]，如果你无法确定商品的分类,商品名称时
                那么调用工具时不要传递相关参数即可;
                拒绝回答涉及政治，暴力，色情或其他你认为的和商品咨询不相关的不合理的问题;
                """;
        // 初始化客户端
        MessageChatMemoryAdvisor messageChatMemoryAdvisor =ragComponent.buildMessageChatMemoryAdvisor();

        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = ragComponent.buildRetrievalAugmentationAdvisor();

        RetrievalRerankAdvisor retrievalRerankAdvisor = ragComponent.buildRetrievalRerankAdvisor();

        this.chatClient= ChatClient.builder(dashScopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(new SimpleLoggerAdvisor(), messageChatMemoryAdvisor,retrievalAugmentationAdvisor,retrievalRerankAdvisor)
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
                .stream().content();
    }
}






