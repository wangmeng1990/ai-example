package com.wm.ai.service;

import com.wm.ai.model.ChatSession;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wm.ai.model.vo.ChatSessionVO;

import java.util.List;

public interface ChatSessionService extends IService<ChatSession>{

    void create(ChatSession chatSession);

    void delete(String sessionId);

    ChatSessionVO detail(String sessionId);

    List<ChatSession> listUserSession(String userId);
}
