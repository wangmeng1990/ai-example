package com.wm.ai.agent.react;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class ToolCallAgent extends ReActAgent{

    private ToolCallback[] availableTools;

    private ChatResponse toolCallChatResponse;

    private final ToolCallingManager toolCallingManager;

    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] tools) {
        super();
        this.toolCallingManager=ToolCallingManager.builder().build();
        this.availableTools = tools;
        //禁用DashScope的工具调用逻辑，自主实现工具调用逻辑
        this.chatOptions= DashScopeChatOptions.builder()
                .withProxyToolCalls(true)
                .build();
    }

    @Override
    public boolean think() {
        try {
            if (StrUtil.isNotBlank(getNextStepPrompt())){
                getMessageList().add(new UserMessage(getNextStepPrompt()));
            }
            List<Message> messageList = getMessageList();
            Prompt prompt=new Prompt(messageList,this.chatOptions);
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .tools(availableTools)
                    .call()
                    .chatResponse();

            this.toolCallChatResponse=chatResponse;

            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();

            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
            String assistantMessageText = assistantMessage.getText();
            log.info(getName()+"思考内容："+assistantMessageText);

            if (CollUtil.isNotEmpty(toolCallList)){
                String toolCallInfo = toolCallList.stream().map(toolCall -> String.format("工具名称：%s，参数%s",toolCall.name(),toolCall.arguments()))
                        .collect(Collectors.joining("\n"));
                log.info(getName()+"选择的工具："+toolCallInfo);
            }

            if (CollUtil.isNotEmpty(toolCallList)){
                return true;
            }
            getMessageList().add(assistantMessage);
        } catch (Exception e) {
            log.error(getName()+"think error",e);
            getMessageList().add(new AssistantMessage("think 出现错误："+e.getMessage()));
        }
        return false;
    }

    @Override
    public String action() {
        if (!this.toolCallChatResponse.hasToolCalls()){
            return "no tool calls";
        }
        Prompt prompt = new Prompt(getMessageList(),this.chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);

        setMessageList(toolExecutionResult.conversationHistory());

        ToolResponseMessage toolCallMessage = (ToolResponseMessage)CollUtil.getLast(getMessageList());

        boolean terminate = toolCallMessage.getResponses().stream().anyMatch(toolResponse -> toolResponse.name().equals("terminate"));
        if (terminate){
            setState(AgentState.FINISHED);
        }

        return toolCallMessage.getResponses().stream().map(toolResponse -> String.format("工具名称：%s，执行结果%s", toolResponse.name(), toolResponse.responseData()))
                .collect(Collectors.joining("\n"));
    }
}
