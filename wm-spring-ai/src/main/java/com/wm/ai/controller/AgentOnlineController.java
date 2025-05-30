package com.wm.ai.controller;

import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgent;
import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgentOptions;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 调用阿里百炼平台 agent
 */
@RestController
@RequestMapping("/online")
@Tag(name = "在线智能体调用")
public class AgentOnlineController {

    @Autowired
    private DashScopeAgent dashScopeAgent;

    @Autowired
    private ChatModel chatModel;


    @Value("${spring.ai.dash-scope.api-key}")
    private String appKey;

    //智能体id
    private final String appId="eee2a6ab210845bfbb5daba0e3caa061";

    @GetMapping("/agent")
    public String agent(@RequestParam String userInput) {

        ChatResponse response = dashScopeAgent.call(new Prompt(userInput, DashScopeAgentOptions.builder().withAppId(appId).build()));
        AssistantMessage app_output = response.getResult().getOutput();
        return app_output.getText();
    }

    /**
     * 百炼在线知识库
     * @param userInput
     * @return
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String userInput) {

        ChatClient chatClient = ChatClient.builder(chatModel).build();

        DashScopeApi dashScopeApi=new DashScopeApi(appKey);

        //文档检索：从在线知识库获取数据
        DocumentRetriever retriever = new DashScopeDocumentRetriever(dashScopeApi,
                DashScopeDocumentRetrieverOptions.builder().withIndexName("人物介绍").build());

        QueryTransformer queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(chatClient.mutate())
                .build();

        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor =RetrievalAugmentationAdvisor.builder()
                .queryTransformers(queryTransformer)
                .documentRetriever(retriever)
                .build();

        return chatClient.prompt()
                .user(userInput)
                .advisors(retrievalAugmentationAdvisor)
                .call().content();
    }
}
