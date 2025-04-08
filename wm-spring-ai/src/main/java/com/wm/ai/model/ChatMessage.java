package com.wm.ai.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wm.ai.model.base.BaseModel;
import lombok.Data;
import org.springframework.ai.chat.messages.MessageType;


@Data
@TableName(value = "chat_message")
public class ChatMessage extends BaseModel {

    /**
     * 会话id
     */
    private String sessionId;

    /**
     * 父级消息id
     */
    private String parentId;
    /**
     * 消息类型
     */
    private MessageType type;

    /**
     * 消息内容
     */
    private String textContent;

    /**
     * 媒体内容如图片链接、语音链接
     */
    private String medias;

    private Integer messageOrder;
}