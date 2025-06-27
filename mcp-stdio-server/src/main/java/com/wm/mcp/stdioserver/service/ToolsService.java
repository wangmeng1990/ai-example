package com.wm.mcp.stdioserver.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.wm.mcp.stdioserver.model.Good;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToolsService {

    @Autowired
    private GoodService goodService;

    @Tool(description = "获取商品信息")
    public String getGoodInfo(@ToolParam(description = "商品名称",required = false) String productName,
                             @ToolParam(description = "商品分类：[服饰鞋包、数码、化妆品]",required = false) String category) {


        List<Good> list = goodService.lambdaQuery()
                .like(StrUtil.isNotEmpty(productName), Good::getName, productName)
                .or()
                .eq(StrUtil.isNotEmpty(category), Good::getCategory, category)
                .list();
        if (CollUtil.isNotEmpty(list)){
            return "good_list:"+JSON.toJSONString(list);
        }
        return "暂无产品信息";
    }
}
