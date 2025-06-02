package com.wm.mcp.stdioserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wm.mcp.stdioserver.mapper.ProductMapper;
import com.wm.mcp.stdioserver.model.Product;
import com.wm.mcp.stdioserver.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateProduct(String code, String name) {
        Product one = lambdaQuery().eq(Product::getCode, code).one();
        if (one==null){
            return "无法根据产品code获取产品，请确认是否提供了正确的产品code";
        }
        boolean update = lambdaUpdate().eq(Product::getCode, code).set(Product::getName, name).update();
        return update?"修改成功":"修改失败";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addProduct(String code, String name) {
        Product one = new Product();
        one.setCode(code);
        one.setName(name);
        save(one);
        return "添加成功";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String delProduct(String code) {
        boolean remove = lambdaUpdate().eq(Product::getCode, code).remove();
        return remove?"删除成功":"删除失败";
    }
}
