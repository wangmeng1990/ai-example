package com.wm.mcp.stdioserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wm.mcp.stdioserver.model.Product;

public interface ProductService extends IService<Product> {

    String updateProduct(String code, String name);

    String addProduct(String code, String name);

    String delProduct(String code);
}
