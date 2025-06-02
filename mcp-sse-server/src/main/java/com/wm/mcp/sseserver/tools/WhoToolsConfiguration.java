package com.wm.mcp.sseserver.tools;

import com.wm.mcp.sseserver.service.WhoTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WhoToolsConfiguration {

    @Bean
    public ToolCallbackProvider whoTools(WhoTool calcTool) {
        return MethodToolCallbackProvider.builder().toolObjects(calcTool).build();
    }
}
