package com.wm.ai.controller;

import com.wm.ai.service.impl.VectorStoreService;
import io.swagger.annotations.Api;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api("ai-VectorStore")
@RestController
@RequestMapping("/vector-store")
public class VectorStoreController {

    @Autowired
    private VectorStoreService vectorStoreService;

    @PostMapping("/store1")
    public String store1(@RequestParam(name = "text") String text) {
        return vectorStoreService.store1(text);
    }

    @PostMapping("/store2")
    public String store2(@RequestParam(name = "file") MultipartFile file){
        return vectorStoreService.store2(file);
    }

    @PostMapping("/store3")
    public List<Document> store3(@RequestParam(name = "userInput") String userInput){
        return vectorStoreService.store3(userInput);
    }

}
