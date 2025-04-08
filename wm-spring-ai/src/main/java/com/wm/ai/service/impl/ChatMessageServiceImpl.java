package com.wm.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import com.wm.ai.exception.BusinessException;
import com.wm.ai.exception.ErrorCode;
import com.wm.ai.model.ChatSession;
import com.wm.ai.service.ChatSessionService;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wm.ai.mapper.AiMessageMapper;
import com.wm.ai.model.ChatMessage;
import com.wm.ai.service.ChatMessageService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatMessageServiceImpl extends ServiceImpl<AiMessageMapper, ChatMessage> implements ChatMessageService {

    @Autowired
    @Lazy
    private ChatSessionService chatSessionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delBySessionId(String sessionId) {
        lambdaUpdate()
            .eq(ChatMessage::getSessionId, sessionId)
            .remove();
    }

    @Override
    public List<ChatMessage> getBySessionId(String sessionId) {
        return lambdaQuery()
            .eq(ChatMessage::getSessionId, sessionId)
            .orderByAsc(ChatMessage::getMessageOrder)
            .orderByAsc(ChatMessage::getCreatedTime)
            .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMessage(ChatMessage chatMessage) {

        ChatSession chatSession = null;
        if (StrUtil.isNotEmpty(chatMessage.getSessionId())){
            chatSession = chatSessionService.getById(chatMessage.getSessionId());
        }
        if (null==chatSession){
            throw new BusinessException("会话不存在", ErrorCode.FAIL);
        }
        if (StrUtil.isEmpty(chatMessage.getTextContent())){
            throw new BusinessException("消息为空", ErrorCode.FAIL);
        }
        if (chatMessage.getType()==null){
            throw new BusinessException("消息类型为空", ErrorCode.FAIL);
        }
        ChatMessage lastMessage = lambdaQuery()
            .eq(ChatMessage::getSessionId, chatMessage.getSessionId())
            .orderByDesc(ChatMessage::getMessageOrder)
            .last("limit 1")
            .one();
        if (lastMessage!=null){
            chatMessage.setMessageOrder(lastMessage.getMessageOrder()+1);
            chatMessage.setParentId(lastMessage.getId());
        }else {
            chatMessage.setMessageOrder(1);
        }
        save(chatMessage);
    }
}
