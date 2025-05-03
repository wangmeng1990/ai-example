package com.wm.ai.controller;

import com.wm.ai.service.impl.EmbeddingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "ai-embedding")
@RestController
@RequestMapping("/embedding")
public class EmbeddingController {
    @Autowired
    private EmbeddingService embeddingService;

    /**
     * 文本嵌入
     * @param text
     * @return
     */
    @GetMapping("/embed1")
    public float[] embed1(@RequestParam(name = "text") String text) {
        return embeddingService.embed1(text);
    }

    /**
     * 文件嵌入
     * @param file
     * @return
     */
    @PostMapping("/embed2")
    public void embed2(@RequestParam(name = "file") MultipartFile file) {
        embeddingService.embed2(file);
    }
}
