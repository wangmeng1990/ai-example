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
import com.wm.ai.tools.DocumentTools;
import com.wm.ai.tools.WebScrapingTool;
import com.wm.ai.tools.WebSearchTool;
import com.wm.ai.util.ClassUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.*;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.expansion.QueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO Rerank
 */
@Service
@AllArgsConstructor
@Data
public class ChatService {

    private final ChatClient chatClient;
    private final MessageWindowChatMemory messageWindowChatMemory;
    private final VectorStore vectorStore;
    private final ObjectMapper objectMapper;
    private final MessageChatMemory messageChatMemory;
    private final ImageModel imageModel;
    private final RerankModel rerankModel;
    private final DocumentService documentService;
    private final ChatClient.Builder chatClientBuilder;

    private final ToolCallbackProvider toolCallbackProvider;
    private final WebSearchTool webSearchTool;

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

        MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(messageWindowChatMemory)
                .conversationId(request.sessionId())
                .order(5)
                .build();
        return chatClient.prompt(request.userInput())
            .advisors(messageChatMemoryAdvisor)
            .stream().content().map(content -> ServerSentEvent.builder(content).event("userInput").build())
            //问题回答结速标识,以便前端消息展示处理
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
        List<Document> documents = documentService.document6();
        if(CollUtil.isNotEmpty(documents)){
            vectorStore.add(documents);
        }
        QuestionAnswerAdvisor questionAnswerAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder().similarityThreshold(0.5d).topK(10).build())
                .build();
        return chatClient.prompt().advisors(spec->spec.param("id","1"))
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
        VectorStoreChatMemoryAdvisor vectorStoreChatMemoryAdvisor =VectorStoreChatMemoryAdvisor.builder(vectorStore)
                .conversationId(request.sessionId())
                .defaultTopK(5)
                .build();
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

        PromptChatMemoryAdvisor PromptChatMemoryAdvisor = org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor.builder(messageWindowChatMemory)
                .conversationId(request.sessionId())
                .order(10)
                .build();
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
            //升级1.0.0弃用
            //.functions(ClassUtil.getFunctions(DocumentReaderFunction.class, CpuFunction.class))
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


        MessageChatMemoryAdvisor messageChatMemoryAdvisor=MessageChatMemoryAdvisor.builder(messageChatMemory)
                .conversationId(request.sessionId())
                .order(10)
                .build();

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
        MessageChatMemoryAdvisor messageChatMemoryAdvisor =MessageChatMemoryAdvisor.builder(messageChatMemory)
                .conversationId(sessionId)
                .order(10)
                .build();
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

        //查询转换:去干扰，语义增强等
        QueryTransformer queryTransformer = RewriteQueryTransformer.builder()
            .chatClientBuilder(chatClient.mutate())
            .targetSearchSystem("vector store")
            .build();

        //多语言支持
        TranslationQueryTransformer translationQueryTransformer = TranslationQueryTransformer.builder()
                .chatClientBuilder(chatClient.mutate())
                .targetLanguage("英语")
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
            //redisStack 的条件检索不支持或者有bug TODO
            //.filterExpression(new FilterExpressionBuilder().eq("excerpt_keywords","八路军").build())
            .build();


        //1.执行QueryTransformer 增强提示词
        //2.执行QueryExpander 扩展提问：一个问题生成多种问法Query，旨在不失本意的前提下扩大检索范围
        //3.执行DocumentRetriever 进行相似度检索：为步骤2的每个Query生成检索结果
        //4.执行DocumentJoiner 合并所有Query的检索结果
        //5.执行QueryAugmenter 上下文查询增强
        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor =RetrievalAugmentationAdvisor.builder()
            .queryTransformers(queryTransformer,translationQueryTransformer)
            .queryExpander(queryExpander)
            .documentRetriever(documentRetriever)
            //知识库无法召回任何信息时，通过拒识进行自定义回答
            .queryAugmenter(ContextualQueryAugmenter.builder()
                    .allowEmptyContext(false)
                    .emptyContextPromptTemplate(new PromptTemplate("需要输出：抱歉，请联系wm"))
                    .build())
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
        PromptTemplate USER_TEXT_TEMPLATE = new PromptTemplate(USER_TEXT_ADVISE);

        RetrievalRerankAdvisor retrievalRerankAdvisor= new RetrievalRerankAdvisor(vectorStore,rerankModel,searchRequest,USER_TEXT_TEMPLATE,0.0,2);

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
                2. 负责产品查询
                3. 分析客户询问意图，推荐相关产品
                -----------------
                
                所有工具不要暴漏参数编码
               
                用户删除产品时，需要用户提供产品编码，并确保和用户确认后才能执行删除
                当需要调用[修改产品，新增产品的]的工具时,要求客户提供工具要求的必录参数
                
                如果用户问题与你负责的功能不相关，请不要做出回答
                """;

        MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(messageWindowChatMemory)
                .conversationId(request.sessionId())
                .order(50)
                .build();
        return chatClient.prompt()
            .system(sysTxt)
            .user(request.userInput())
            .toolCallbacks(toolCallbackProvider)
            .advisors(messageChatMemoryAdvisor)
            .stream().content().collect(Collectors.joining())
            .onErrorResume(e -> {
                System.err.println("Error occurred during chat: " + e.getMessage());
                return Mono.just("Error: " + e.getMessage());
            });
    }

    public Mono<String> chat15(ChatController.ChatRequest request) {
        String sysTxt="""
                你是一个负责产品管理和读取文档的智能体，功能如下：
                -----------------
                1. 负责产品的增删改查
                2. 负责产品查询
                3. 分析客户询问意图，推荐相关产品
                4. 读取本地文档
                -----------------
                
                所有工具不要暴漏参数编码
               
                用户删除产品时，需要用户提供产品编码，并确保和用户确认后才能执行删除
                当需要调用[修改产品，新增产品的]的工具时,要求客户提供工具要求的必录参数
                当用户需要查询产品时，你只能提供工具返回的产品
                当用户需要读取文件的内容时，请调用读取文件内容工具
                
                如果用户问题与你负责的功能不相关，你可以根据需要使用对应的工具获取回答
                """;

        MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(messageWindowChatMemory)
                .conversationId(request.sessionId())
                .order(50)
                .build();
        return chatClient.prompt()
            .system(sysTxt)
            .user(request.userInput())
            .tools(new DocumentTools(documentService),webSearchTool,new WebScrapingTool())
            .toolCallbacks(toolCallbackProvider)
            /**
             * @see ToolCallback#call(String, ToolContext) 默认实现不支持toolContext
             *
             * @see  MethodToolCallback 重写了call，支持toolContext
             *
             * @see org.springframework.ai.mcp.SyncMcpToolCallback 不支持toolContext
             *
             * 一些第三方工具封装为SyncMcpToolCallback，不能设置toolContext,否则会抛异常，MethodToolCallback和SyncMcpToolCallback不能
             * 一起使用有点不合理
             */
            .toolContext(Map.of("userId",request.sessionId()))
            .advisors(messageChatMemoryAdvisor)
            .stream().content().collect(Collectors.joining())
            .onErrorResume(e -> {
                System.err.println("Error occurred during chat: " + e.getMessage());
                return Mono.just("Error: " + e.getMessage());
            });
    }
}