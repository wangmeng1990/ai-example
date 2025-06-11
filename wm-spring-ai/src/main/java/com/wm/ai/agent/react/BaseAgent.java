package com.wm.ai.agent.react;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 主循环loop
 */
@Slf4j
@Data
public abstract class BaseAgent {
    private String name;
    private String systemPrompt;
    private String nextStepPrompt;
    private AgentState state= AgentState.IDEL;

    private int currentStep=0;
    private int maxStep=10;

    private ChatClient chatClient;

    private List<Message> messageList=new ArrayList<>();

    public String run(String userPrompt){
       try {
           if(this.state!=AgentState.IDEL){
               throw new RuntimeException("Current state is not IDEL");
           }
           if(StrUtil.isEmpty(userPrompt)){
               throw new RuntimeException("userPrompt is empty");
           }
           this.state = AgentState.RUNNING;

           this.messageList.add(new UserMessage(userPrompt));

           List<String> results=new ArrayList<>();

           for (int i = 1; i < maxStep&&state!=AgentState.FINISHED; i++) {
               this.currentStep=i;
               log.info("执行步骤 {}/{}", currentStep, maxStep);
               String stepResult=step();
               results.add("step"+currentStep+":"+stepResult);
           }
           if (currentStep>=maxStep){
               this.state = AgentState.FINISHED;
               log.info("智能体【{}】执行{}步后完成", name, maxStep);
               results.add("执行"+maxStep+"步后完成");
           }
           return String.join("\n", results);
       }catch (Exception e){
           state=AgentState.ERROR;
           log.error("执行异常：", e);
           return "系统异常："+e.getMessage();
       }finally {
           this.cleanup();
       }
    }

    protected void cleanup() {
    }

    public abstract String step();
}
