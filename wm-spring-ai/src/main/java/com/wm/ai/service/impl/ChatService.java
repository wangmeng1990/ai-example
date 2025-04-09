package com.wm.ai.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.advisor.RetrievalRerankAdvisor;
import com.alibaba.cloud.ai.model.RerankModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wm.ai.conf.MessageChatMemory;
import com.wm.ai.controller.ChatController;
import com.wm.ai.functioncall.CpuFunction;
import com.wm.ai.functioncall.DocumentReaderFunction;
import com.wm.ai.util.ClassUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.*;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.expansion.QueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

/**
 * TODO Rerank
 */
@Service
@AllArgsConstructor
@Data
public class ChatService {

    private final ChatClient chatClient;
    private final InMemoryChatMemory inMemoryChatMemory;
    private final VectorStore vectorStore;
    private final ObjectMapper objectMapper;
    private final MessageChatMemory messageChatMemory;
    private final ImageModel imageModel;
    private final RerankModel rerankModel;
    private final DocumentService documentService;
    private final ChatClient.Builder chatClientBuilder;

    private final ToolCallbackProvider toolCallbackProvider;

    /**
     *call
     * @param userInput
     * @return
     */
    public String chat1(String userInput) {
        return this.chatClient.prompt()
            .user(userInput)
            .call()
            .content();
    }

    /**
     * stream
     * @param request
     * @return
     */
    public Mono<String> chat2(ChatController.ChatRequest request) {
        return chatClient.prompt()
            .user(request.userInput())
            .stream().content().collect(Collectors.joining())
            //问题回答结速标识,以便前端消息展示处理
            .onErrorResume(e -> {
                System.err.println("Error occurred during chat: " + e.getMessage());
                return Mono.just("Error: " + e.getMessage());
            });
    }

    /**
     *
     * 基于历史消息
     * 可以使用InMemoryChatMemory或自定义实现ChatMemory
     */
    public Flux<ServerSentEvent<String>> chat3(ChatController.ChatRequest request) {
        return chatClient.prompt(request.userInput())
            .advisors(new MessageChatMemoryAdvisor(inMemoryChatMemory, request.sessionId(), 5))
            .stream().content().map(content -> ServerSentEvent.builder(content).event("userInput").build())
            //问题回答结速标识,以便前端消息展示处理
            .concatWithValues(ServerSentEvent.builder("[DONE]").event("finish").build())
            .onErrorResume(e -> Flux.just(ServerSentEvent.builder("Error: " + e.getMessage()).event("error").build()));
    }

    /**
     * 通过ChatResponse获取其他属性
     * @param request
     * @return
     */
    public Flux<ServerSentEvent<String>> chat4(ChatController.ChatRequest request) {
        String userId = request.sessionId();
        //被追问消息
        UserMessage followMessage = new UserMessage(request.followMessage());
        return chatClient.prompt()
            .user(request.userInput())
            .messages(CollUtil.newArrayList(followMessage))
            .advisors(new MessageChatMemoryAdvisor(inMemoryChatMemory, userId, 10))
            .stream().chatResponse()
            .map(chatResponse -> ServerSentEvent.builder(chatResponse.getResult().getOutput().getText()).event("userInput").build())
            //.map(response -> ServerSentEvent.builder(toJson(response)).event("userInput").build());
            .concatWithValues(ServerSentEvent.builder("[DONE]").event("finish").build())
            .onErrorResume(e -> Flux.just(ServerSentEvent.builder("Error: " + e.getMessage()).event("error").build()));
    }

    /**
     * 返回指定类型实体
     * @param request
     * @return
     */
    public ChatController.MyChatResponse chat5(ChatController.ChatRequest request) {
        return chatClient.prompt()
            .user(request.userInput())
            .call()
            .entity(ChatController.MyChatResponse.class);
    }

    /**
     * QuestionAnswerAdvisor:从向量存储(知识库、文档、FAQ等)中检索与用户输入相关的内容，作为提示词的一部分来增强回答的质量
     * @param request
     * @return
     */
    public Mono<String> chat6(ChatController.ChatRequest request) {
        //查找相似度阈值>=0.5（越大匹配越严格）的前 5 个记录
        //RAG Advisor
        QuestionAnswerAdvisor questionAnswerAdvisor = new QuestionAnswerAdvisor(vectorStore,
            SearchRequest.builder().similarityThreshold(0.5d).topK(10).build());
        return chatClient.prompt()
            .advisors(questionAnswerAdvisor)
            .user(request.userInput())
            .stream().content().collect(Collectors.joining())
            .onErrorResume(e -> {
                System.err.println("Error occurred during chat: " + e.getMessage());
                return Mono.just("Error: " + e.getMessage());
            });
    }


    /**
     * VectorStoreChatMemoryAdvisor:
     * 会把对话保存到向量数据库
     * 从对话记录中提取信息，获取上下文,增强检索
     * @param request
     * @return
     */
    public Mono<String> chat7(ChatController.ChatRequest request) {
        String systemTextAdvise = """
			使用 LONG_TERM_MEMORY 来提供准确的答案.
			---------------------
			LONG_TERM_MEMORY:
			{long_term_memory}
			---------------------
			""";
        //noinspection removal
        VectorStoreChatMemoryAdvisor vectorStoreChatMemoryAdvisor = new VectorStoreChatMemoryAdvisor(vectorStore,request.sessionId()
            ,100, systemTextAdvise);
        return chatClient.prompt()
            .advisors(vectorStoreChatMemoryAdvisor)
            .user(request.userInput())
            .stream().content().collect(Collectors.joining())
            .onErrorResume(e -> {
                System.err.println("Error occurred during chat: " + e.getMessage());
                return Mono.just("Error: " + e.getMessage());
            });
    }

    /**
     * PromptChatMemoryAdvisor:把ChatMemory的记录汇总作为提示词的一部分
     * @param request
     * @return
     */
    public Mono<String> chat8(ChatController.ChatRequest request) {
        PromptChatMemoryAdvisor PromptChatMemoryAdvisor =new PromptChatMemoryAdvisor(inMemoryChatMemory,request.sessionId(),10, "[MEMORY]");
        return chatClient.prompt()
            .advisors(PromptChatMemoryAdvisor)
            .user(request.userInput())
            .stream().content().collect(Collectors.joining())
            .onErrorResume(e -> {
                System.err.println("Error occurred during chat: " + e.getMessage());
                return Mono.just("Error: " + e.getMessage());
            });
    }

    @SneakyThrows
    public String toJson(ChatResponse chatResponse) {
        return objectMapper.writeValueAsString(chatResponse);
    }

    /**
     * functionCall
     * @param request
     * @return
     */
    public Mono<String> chat9(ChatController.ChatRequest request) {
        return chatClient.prompt()
            .user(request.userInput())
            .functions(ClassUtil.getFunctions(DocumentReaderFunction.class, CpuFunction.class))
            .stream().content().collect(Collectors.joining())
            .onErrorResume(e -> {
                System.err.println("Error occurred during chat: " + e.getMessage());
                return Mono.just("Error: " + e.getMessage());
            });
    }

    /**
     * 对话及会话保存
     * @param request
     * @return
     */
    public Mono<String> chat10(ChatController.ChatRequest request) {

        MessageChatMemoryAdvisor messageChatMemoryAdvisor = new MessageChatMemoryAdvisor(messageChatMemory,
            request.sessionId(), 10);

        return chatClient.prompt()
            .user(request.userInput())
            .advisors(messageChatMemoryAdvisor)
            .stream().content().collect(Collectors.joining())
            .onErrorResume(e -> {
                System.err.println("Error occurred during chat: " + e.getMessage());
                return Mono.just("Error: " + e.getMessage());
            });
    }

    /**
     * 根据上传文件对话 | 文档对话
     * @return
     */
    public Mono<String> chat11(String sessionId, String userInput, MultipartFile file) {
        MessageChatMemoryAdvisor messageChatMemoryAdvisor = new MessageChatMemoryAdvisor(messageChatMemory,
            sessionId, 10);
        String fileContext=getFileContext(file);
        return chatClient.prompt()
            //可以使用system或messages增强提问
            .system(promptSystemSpec->{
                if (StrUtil.isNotEmpty(fileContext)){
                    promptSystemSpec.text(fileContext);
                }
            })
            //.messages(new UserMessage(fileContext))
            .user(userInput)
            .advisors(messageChatMemoryAdvisor)
            .stream().content().collect(Collectors.joining())
            .onErrorResume(e -> {
                System.err.println("Error occurred during chat: " + e.getMessage());
                return Mono.just("Error: " + e.getMessage());
            });
    }

    /**
     * RetrievalAugmentationAdvisor: 检索增强
     * @param request
     * @return
     */
    public Mono<String> chat12(ChatController.ChatRequest request) {

        //1.先经过相似度检索得到最匹配的结果集
        //2.对结果集进行重排序,筛选掉一些不合适的结果
        //3.再使用重排序后的结果集加强prompt检索

        SearchRequest searchRequest = SearchRequest.builder()
            .query(request.userInput())
            .topK(5)
            .build();
        RetrievalRerankAdvisor retrievalRerankAdvisor= new RetrievalRerankAdvisor(vectorStore,rerankModel,searchRequest);

        return chatClient.prompt()
            //.messages(new UserMessage(fileContext))
            .user(request.userInput())
            .advisors(retrievalRerankAdvisor)
            .stream().content().collect(Collectors.joining())
            .onErrorResume(e -> {
                System.err.println("Error occurred during chat: " + e.getMessage());
                return Mono.just("Error: " + e.getMessage());
            });
    }

    private String getFileContext(MultipartFile file) {
        String fileContext = "";
        if(null!=file){
            //TODO 上传文件服务,用于保存会话
            String fileCtx = documentService.document1(file).get(0).getText();
            if (StrUtil.isNotEmpty(fileCtx)){
                fileContext=
                """
                回答时基于以下内容:
                ---------------------
                """+ fileCtx +"""
                ---------------------
                """;
            }
        }
        return fileContext;
    }

    /**
     * RetrievalAugmentationAdvisor:检索增强
     * @param request
     * @return
     */
    public Mono<String> chat13(ChatController.ChatRequest request) {

        //增强提示词:去干扰，语义增强等
        QueryTransformer queryTransformer = RewriteQueryTransformer.builder()
            .chatClientBuilder(chatClient.mutate())
            .targetSearchSystem("vector store")
            .build();

        //提示词扩展:根据提问生成多版本提问
        QueryExpander queryExpander = MultiQueryExpander.builder()
            .chatClientBuilder(chatClient.mutate())
            .numberOfQueries(2)
            .build();

        //向量数据库相似度检索
        DocumentRetriever documentRetriever  = VectorStoreDocumentRetriever.builder()
            .vectorStore(vectorStore)
            .similarityThreshold(0.0)
            .topK(5)
            .build();


        //1.执行QueryTransformer 增强提示词
        //2.执行QueryExpander 扩展提问：一个问题生成多种问法Query，旨在不失本意的前提下扩大检索范围
        //3.执行DocumentRetriever 进行相似度检索：为步骤2的每个Query生成检索结果
        //4.执行DocumentJoiner 合并所有Query的检索结果
        //5.执行QueryAugmenter 汇总信息作为最后的提示词
        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor =RetrievalAugmentationAdvisor.builder()
            .queryTransformers(queryTransformer)
            .queryExpander(queryExpander)
            .documentRetriever(documentRetriever)
            .order(1)
            .build();

        //重排序
        SearchRequest searchRequest = SearchRequest.builder()
            .query(request.userInput())
            .topK(5)
            .build();

        final String USER_TEXT_ADVISE = """
			下面是上下文:
			---------------------
			{question_answer_context}
			---------------------
			根据上下文和提供的历史信息，进行回复。如果上下文中没有答案，请通知用户您无法回答问题。
			""";

        RetrievalRerankAdvisor retrievalRerankAdvisor= new RetrievalRerankAdvisor(vectorStore,rerankModel,searchRequest,USER_TEXT_ADVISE,0.0,true,2);

        return chatClient.prompt()
            .user(request.userInput())
            .advisors(retrievalAugmentationAdvisor,retrievalRerankAdvisor)
            .stream().content().collect(Collectors.joining())
            .onErrorResume(e -> {
                System.err.println("Error occurred during chat: " + e.getMessage());
                return Mono.just("Error: " + e.getMessage());
            });
    }

    /**
     * MCP
     * @param request
     * @return
     */
    public Mono<String> chat14(ChatController.ChatRequest request) {
        String sysTxt="""
                你是一个负责产品管理的智能体，功能如下：
                -----------------
                1. 负责产品的增删改查
                2. 负责产品查询和推荐
                -----------------
                
                需要调用工具时，你必须要求用户提供工具需要的参数，不要暴漏参数编码
                如果用户问题与你负责的功能不相关，请不要做出回答
                """;
        return chatClient.prompt()
            .system(sysTxt)
            .user(request.userInput())
            .tools(toolCallbackProvider)
            .stream().content().collect(Collectors.joining())
            .onErrorResume(e -> {
                System.err.println("Error occurred during chat: " + e.getMessage());
                return Mono.just("Error: " + e.getMessage());
            });
    }
}