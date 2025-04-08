package com.wm.ai.mcp.tools;

import com.wm.ai.mcp.service.DateService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DateToolsConfiguration {
    @Bean
    public ToolCallbackProvider dateTools(DateService dateService) {
        return MethodToolCallbackProvider.builder().toolObjects(dateService).build();
    }
}
