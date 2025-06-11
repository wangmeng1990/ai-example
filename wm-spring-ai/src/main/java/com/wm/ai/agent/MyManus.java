package com.wm.ai.agent;

import com.wm.ai.agent.react.TerminateTool;
import com.wm.ai.agent.react.ToolCallAgent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

@Component
public class MyManus extends ToolCallAgent {

    public MyManus(ToolCallback[] agentTool, ChatModel dashScopeChatModel) {
        super(agentTool);
        setName("MyManus");

        String SYSTEM_PROMPT = """  
                您是 MyManus，一个全能的 AI 助手，旨在解决用户提出的任何任务。
                您可以使用各种工具来高效完成复杂的请求。
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);
        String NEXT_STEP_PROMPT = """  
                根据用户需求，主动选择最合适的工具或工具组合。
                对于复杂的任务，您可以分解问题并逐步使用不同的工具来解决它。
                使用每个工具后，清楚地解释执行结果并建议后续步骤。
                如果要在任何时候停止交互，请使用 'terminate' 工具/函数调用。
                """;
        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        this.setMaxStep(15);
        // 初始化客户端
        ChatClient chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultTools(new TerminateTool())
                .build();
        this.setChatClient(chatClient);
    }
}






