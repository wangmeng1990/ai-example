package com.wm.ai.model.vo;

import com.wm.ai.model.ChatMessage;
import com.wm.ai.model.ChatSession;
import lombok.Data;

import java.util.List;

@Data
public class ChatSessionVO extends ChatSession {
    private List<ChatMessage> ChatMessageList;
}
