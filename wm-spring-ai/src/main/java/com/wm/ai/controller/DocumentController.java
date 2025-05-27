package com.wm.ai.controller;

import com.wm.ai.service.impl.DocumentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "ai-document")
@RestController
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    /**
     * 流读取
     * @param file
     * @return
     */
    @PostMapping("/document1")
    public List<Document> document1(@RequestParam(name = "file") MultipartFile file){
        return documentService.document1(file);
    }

    /**
     * 本地文件读取
     * @param path
     * @return
     */
    @PostMapping("/document2")
    public List<Document> document2(@RequestParam(name = "path") String path){
        return documentService.document2(path);
    }

    /**
     * 读取网络资源
     * @param url
     * @return
     */
    @PostMapping("/document3")
    public List<Document> document3(@RequestParam(name = "url") String url){
        return documentService.document3(url);
    }

    /**
     * 内容直接转化为Document
     * @param context
     * @return
     */
    @PostMapping("/document4")
    public List<Document> document4(@RequestParam(name = "context") String context){
        return documentService.document4(context);
    }

    @PostMapping("/document6")
    public List<Document> document6(){
        return documentService.document6();
    }

    //TODO 自定义分割规则
}
