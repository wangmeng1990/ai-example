package com.wm.ai.mcp.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.wm.ai.mcp.model.Product;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ToolsService {

    @Autowired
    private ProductService productService;

    @Tool(description = "获取产品信息")
    public String getProduct() {
        List<Product> list = productService.list();
        if (CollUtil.isNotEmpty(list)){
            return list.stream()
                       .map(product -> product.getName() + "[" + product.getCode() + "]")
                       .collect(Collectors.joining(","));
        }
        return "暂无产品信息";
    }

    @Tool(description = "修改产品名称")
    public String updateProduct(@ToolParam(description = "产品编码",required = true) String code,
                                @ToolParam(description = "产品名称",required = true) String name) {

        return productService.updateProduct(code,name);
    }

    @Tool(description = "新增产品")
    public String addProduct(@ToolParam(description = "产品编码",required = true) String code,
                                @ToolParam(description = "产品名称",required = true) String name) {
        return productService.addProduct(code,name);
    }

    @Tool(description = "删除产品")
    public String delProduct(@ToolParam(description = "产品编码",required = true) String code) {
        return productService.delProduct(code);
    }
}
