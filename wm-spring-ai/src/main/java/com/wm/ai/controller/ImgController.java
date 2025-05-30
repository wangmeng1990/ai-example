package com.wm.ai.controller;

import com.wm.ai.service.impl.ImgService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ai-img")
@RestController
@RequestMapping("/img")
public class ImgController {

    @Autowired
    private ImgService imgService;

    @PostMapping("/img1")
    public void img1(@RequestParam(name = "userInput") String userInput) {

         imgService.img1(userInput);
    }
}
