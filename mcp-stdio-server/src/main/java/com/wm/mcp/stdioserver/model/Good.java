package com.wm.mcp.stdioserver.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wm.mcp.stdioserver.model.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;


@Data
@TableName(value = "t_good")
@EqualsAndHashCode(callSuper = true)
public class Good extends BaseModel {
    private String name;

    private String code;

    private BigDecimal price;

    private String description;

    private String category;
}
