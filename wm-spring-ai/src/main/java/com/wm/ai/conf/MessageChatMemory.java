package com.wm.ai.conf;

import cn.hutool.core.collection.CollUtil;
import com.wm.ai.model.ChatMessage;
import com.wm.ai.service.ChatMessageService;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageChatMemory implements ChatMemory {

    @Autowired
    private ChatMessageService chatMessageService;


    @Override
    public void add(String conversationId, List<Message> messages) {
        if(CollUtil.isNotEmpty(messages)){
            List<ChatMessage> chatMessageList = messages.stream().map(message -> to(conversationId,message)).collect(Collectors.toList());
            chatMessageService.addMessage(chatMessageList.get(0));
        }
    }

    @Override
    public List<Message> get(String conversationId) {

        List<ChatMessage> list = chatMessageService.lambdaQuery()
            .eq(ChatMessage::getSessionId, conversationId)
            .list();
        List<Message> messages = new ArrayList<>();
        list.forEach(x->{
            messages.add(form(x));
        });
        return messages;
    }

    @Override
    public void clear(String conversationId) {
        chatMessageService.lambdaUpdate()
                .eq(ChatMessage::getSessionId, conversationId)
                .remove();
    }

    private Message form(ChatMessage chatMessage){
        Message message = null;
        if (MessageType.USER.equals(chatMessage.getType())){
            message=new UserMessage(chatMessage.getTextContent());

        }else if (MessageType.ASSISTANT.equals(chatMessage.getType())){
            message=new AssistantMessage(chatMessage.getTextContent());

        }else if (MessageType.SYSTEM.equals(chatMessage.getType())){
            message=new SystemMessage(chatMessage.getTextContent());
        }
        return message;
    }

    private ChatMessage to(String conversationId,Message message){
        ChatMessage toMessage = new ChatMessage();
        toMessage.setTextContent(message.getText());
        toMessage.setSessionId(conversationId);
        toMessage.setType(message.getMessageType());
        return toMessage;
    }

}
