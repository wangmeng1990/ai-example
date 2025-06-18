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

    @Tool(description = "获取产品信息")
    public String getGoodInfo(@ToolParam(description = "产品名称",required = false) String productName,
                             @ToolParam(description = "产品分类",required = false) String category) {


        List<Good> list = goodService.lambdaQuery()
                .like(StrUtil.isNotEmpty(productName), Good::getName, productName)
                .or()
                .eq(StrUtil.isNotEmpty(category), Good::getCategory, category)
                .list();
        if (CollUtil.isNotEmpty(list)){
            return JSON.toJSONString(list);
        }
        return "暂无产品信息";
    }
}
