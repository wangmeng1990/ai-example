package com.wm.ai.controller;

import com.wm.ai.service.impl.VectorStoreService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "ai-VectorStore")
@RestController
@RequestMapping("/vector-store")
public class VectorStoreController {

    @Autowired
    private VectorStoreService vectorStoreService;

    /**
     * embedding 用户画像
     * @return
     */
    @PostMapping("/store/user-portraits")
    public String userPortraits() {
        return vectorStoreService.userPortraits();
    }
}
