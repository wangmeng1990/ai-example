package com.wm.ai.agent;

import com.alibaba.cloud.ai.advisor.RetrievalRerankAdvisor;
import com.alibaba.cloud.ai.model.RerankModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.expansion.QueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Data
public class RagComponent {

    private final ChatModel dashScopeChatModel;
    private final ChatMemory messageWindowChatMemory;
    private final VectorStore vectorStore;
    private final RerankModel rerankModel;


    /**
     * 会话存储
     * @return
     */
    public MessageChatMemoryAdvisor buildMessageChatMemoryAdvisor() {
       return MessageChatMemoryAdvisor.builder(messageWindowChatMemory)
                .conversationId("1")//会话id
                .order(5)
                .build();
    }

    public RetrievalAugmentationAdvisor buildRetrievalAugmentationAdvisor() {

        ChatClient.Builder chatClient = ChatClient.builder(dashScopeChatModel);
        //查询转换
//        QueryTransformer queryTransformer = RewriteQueryTransformer.builder()
//                .chatClientBuilder(chatClient)
//                .build();

        //提示词扩展:根据提问生成多版本提问
//        QueryExpander queryExpander = MultiQueryExpander.builder()
//                .chatClientBuilder(chatClient)
//                .numberOfQueries(2)
//                .build();

        //向量数据库相似度检索
        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .similarityThreshold(0.5)
                .topK(5)
                //redisStack 的条件检索不支持或者有bug TODO
                //.filterExpression(new FilterExpressionBuilder().eq("userId","1").build())
                .build();


        //1.执行QueryTransformer 增强提示词
        //2.执行QueryExpander 扩展提问：一个问题生成多种问法Query，旨在不失本意的前提下扩大检索范围
        //3.执行DocumentRetriever 进行相似度检索：为步骤2的每个Query生成检索结果
        //4.执行DocumentJoiner 合并所有Query的检索结果
        //5.执行QueryAugmenter 上下文查询增强
        //知识库无法召回任何信息时，通过拒识进行自定义回答

        return RetrievalAugmentationAdvisor.builder()
                //.queryTransformers(queryTransformer)
                //.queryExpander(queryExpander)
                .documentRetriever(documentRetriever)
                //知识库无法召回任何信息时，通过拒识进行自定义回答
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(false)
                        .emptyContextPromptTemplate(new PromptTemplate("抱歉，暂时无法找到符合您要求的商品"))
                        .build())
                .order(1)
                .build();
    }

    public RetrievalRerankAdvisor buildRetrievalRerankAdvisor() {

        //重排序
        SearchRequest searchRequest = SearchRequest.builder()
                .topK(3)
                .build();

        final String USER_TEXT_ADVISE = """
			下面是上下文:
			---------------------
			{question_answer_context}
			---------------------
			根据上下文和提供的历史信息，而不是先验知识，来回答用户问题
			""";
        PromptTemplate USER_TEXT_TEMPLATE = new PromptTemplate(USER_TEXT_ADVISE);

        return new RetrievalRerankAdvisor(vectorStore,rerankModel,searchRequest,USER_TEXT_TEMPLATE,0.5,2);
    }

    public QuestionAnswerAdvisor buildQuestionAnswerAdvisor() {

        return new QuestionAnswerAdvisor(vectorStore);
    }
}
