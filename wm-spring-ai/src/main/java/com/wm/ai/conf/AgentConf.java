package com.wm.ai.conf;

import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgent;
import com.alibaba.cloud.ai.dashscope.api.DashScopeAgentApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConf {

    @Bean
    public DashScopeAgent dashScopeAgent(DashScopeAgentApi dashScopeAgentApi) {
        return new DashScopeAgent(dashScopeAgentApi);
    }
}
