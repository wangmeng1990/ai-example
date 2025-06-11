package com.wm.ai.agent;

import com.wm.ai.tools.DocumentTools;
import com.wm.ai.tools.WebSearchTool;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolRegister {

    @Resource
    private WebSearchTool webSearchTool;

    @Resource
    private DocumentTools documentTools;


    @Bean
    public ToolCallback[] agentTool() {
        return ToolCallbacks.from(webSearchTool,documentTools);
    }
}
