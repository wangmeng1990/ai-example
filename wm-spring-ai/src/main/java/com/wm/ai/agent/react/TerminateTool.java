package com.wm.ai.agent.react;


import org.springframework.ai.tool.annotation.Tool;

/**
 * agent 终止工具
 */
public class TerminateTool {

    @Tool(description = "如果你认为任务已完成或者无法完成就调用此工具来终止任务")
    public String terminate() {
        return "任务结束";
    }
}
