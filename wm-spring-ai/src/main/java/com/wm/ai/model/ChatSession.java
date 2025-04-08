package com.wm.ai.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wm.ai.model.base.BaseModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@TableName(value = "chat_session")
public class ChatSession extends BaseModel {

    private String name;

    @NotNull
    private String userId;

}