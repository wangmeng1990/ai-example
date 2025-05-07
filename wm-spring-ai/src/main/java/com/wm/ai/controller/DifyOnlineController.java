package com.wm.ai.controller;

import com.wm.ai.service.impl.DifyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dify")
@Tag(name = "dify在线调用")
public class DifyOnlineController {

    @Autowired
    private DifyService difyService;

    @RequestMapping("/chat")
    public String chat(String userInput) {
        return difyService.chat(userInput);
    }
}
