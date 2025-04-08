package com.wm.ai.mcp.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;


@Service
public class DateService {

    @Tool(description = "获取产品信息")
    public String getCurrentDate() {
        return "MCP服务，孩子说很好吃";
    }
}
