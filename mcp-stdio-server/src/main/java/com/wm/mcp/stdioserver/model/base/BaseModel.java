package com.wm.mcp.stdioserver.model.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class BaseModel implements java.io.Serializable{
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    private Date createdTime=new Date();

    private Date editedTime=new Date();

    private String creatorId;

    private String editorId;
}
