package com.wm.mcp.stdioserver.tools;

import com.wm.mcp.stdioserver.service.ToolsService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductToolsConfiguration {
    @Bean
    public ToolCallbackProvider productTools(ToolsService toolsService) {
        return MethodToolCallbackProvider.builder().toolObjects(toolsService).build();
    }
}
