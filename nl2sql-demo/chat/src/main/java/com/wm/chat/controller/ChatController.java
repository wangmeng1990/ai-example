
package com.wm.chat.controller;

import com.alibaba.cloud.ai.dbconnector.DbConfig;
import com.alibaba.cloud.ai.request.SchemaInitRequest;
import com.alibaba.cloud.ai.service.analytic.AnalyticNl2SqlService;
import com.alibaba.cloud.ai.service.simple.SimpleNl2SqlService;
import com.alibaba.cloud.ai.service.simple.SimpleVectorStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
public class ChatController {

    @Autowired
    private AnalyticNl2SqlService nl2SqlService;

    @Autowired
    private SimpleNl2SqlService simpleNl2SqlService;

    @Autowired
    private SimpleVectorStoreService simpleVectorStoreService;

    @Autowired
    private DbConfig dbConfig;

    @PostMapping("/chat")
    public String nl2Sql(@RequestBody String input) throws Exception {
        return nl2SqlService.nl2sql(input);
    }

    @PostMapping("/simpleChat")
    public String simpleNl2Sql(@RequestBody String input) throws Exception {
        SchemaInitRequest schemaInitRequest = new SchemaInitRequest();
        schemaInitRequest.setDbConfig(dbConfig);
        // 这里设置你需要查询的表
        schemaInitRequest.setTables(Arrays.asList("tableName"));
        simpleVectorStoreService.schema(schemaInitRequest);
        return simpleNl2SqlService.nl2sql(input);
    }
}

