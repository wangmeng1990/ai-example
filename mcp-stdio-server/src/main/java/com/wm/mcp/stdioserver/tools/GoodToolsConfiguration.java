package com.wm.mcp.stdioserver.tools;

import com.wm.mcp.stdioserver.service.ToolsService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoodToolsConfiguration {
    @Bean
    public ToolCallbackProvider goodTools(ToolsService toolsService) {
        return MethodToolCallbackProvider.builder().toolObjects(toolsService).build();
    }
}
