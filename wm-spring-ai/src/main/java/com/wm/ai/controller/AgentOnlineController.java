package com.wm.ai.controller;

import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgent;
import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgentOptions;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
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

    //智能体id
    private final String appId="eee2a6ab210845bfbb5daba0e3caa061";

    @GetMapping("/agent")
    public String agent(@RequestParam String userInput) {

        ChatResponse response = dashScopeAgent.call(new Prompt(userInput, DashScopeAgentOptions.builder().withAppId(appId).build()));
        AssistantMessage app_output = response.getResult().getOutput();
        return app_output.getText();
    }
}
