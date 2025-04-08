package com.wm.ai.service;

import com.wm.ai.model.ChatMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ChatMessageService extends IService<ChatMessage>{

    void delBySessionId(String sessionId);

    List<ChatMessage> getBySessionId(String sessionId);

    void addMessage(ChatMessage chatMessage);
}
