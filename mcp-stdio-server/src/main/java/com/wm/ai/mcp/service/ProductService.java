package com.wm.ai.mcp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wm.ai.mcp.model.Product;

public interface ProductService extends IService<Product> {

    String updateProduct(String code, String name);

    String addProduct(String code, String name);

    String delProduct(String code);
}
