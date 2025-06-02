package com.wm.mcp.sseserver.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;


@Service
public class WhoTool {

    @Tool(description = "wm的信息")
    public String who(@ToolParam(description = "用户输入") String a) {
        return "核动力牛马,喜欢唱跳和codeing";
    }
}
