package com.wm.ai.conf;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.autoconfigure.ToolCallingAutoConfiguration;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.context.annotation.Bean;

/**
 * 工具调用过程
 *
 * @see ToolCallingAutoConfiguration
 * 可以自定义：ToolCallbackResolver，ToolExecutionExceptionProcessor，ToolCallingManager 来定制工具调用过程比如完善日志，追踪调用进度等
 * ollama等 使用了以上方式
 * @see org.springframework.ai.ollama.OllamaChatModel#call(Prompt)
 * @see org.springframework.ai.ollama.OllamaChatModel#internalCall(Prompt, ChatResponse)
 * @see org.springframework.ai.ollama.OllamaChatModel#stream(Prompt)
 * @see org.springframework.ai.ollama.OllamaChatModel#internalStream(Prompt, ChatResponse)
 *
 * 阿里DashScope是另一种实现：
 * @see DashScopeChatModel#call(Prompt)
 * @see DashScopeChatModel#stream(Prompt)
 */
//@Configuration
//@AutoConfigureBefore( value = {ToolCallingAutoConfiguration.class})
public class ToolCallConf {

    @Bean
    ToolExecutionExceptionProcessor customToolExceptionProcessor() {
        return exception -> {
            if (exception.getCause() instanceof IllegalArgumentException) {
                return "参数异常";

            }else if(exception.getCause() instanceof RuntimeException){
                return "服务器异常";

            }
            return "Error tool execution: " + exception.getMessage();
        };
    }

}
