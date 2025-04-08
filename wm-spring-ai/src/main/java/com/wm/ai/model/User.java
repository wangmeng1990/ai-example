package com.wm.ai.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wm.ai.model.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@TableName(value = "user")
@EqualsAndHashCode(callSuper = true)
public class User extends BaseModel {

    // 昵称
    private String nickName;

    // 头像
    private String avatar;

    private String gender;

    private String phone;

    private String passWord;
}