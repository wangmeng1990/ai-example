package com.wm.ai.agent.react;

import lombok.extern.slf4j.Slf4j;

/**
 * 负责reasoning和action
 */
@Slf4j
public abstract class ReActAgent extends BaseAgent {


    public abstract boolean think();

    public abstract String action();

    @Override
    public String step() {
       try {
           if (think()){
               return action();
           }
           return "无需行动";
       }catch (Exception e) {
           log.error("执行step异常：",e);
           return "步骤执行失败";
       }
    }
}
