package com.wm.ai.mcp.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wm.ai.mcp.model.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@TableName(value = "t_product")
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseModel {
    private String name;

    private String code;
}
