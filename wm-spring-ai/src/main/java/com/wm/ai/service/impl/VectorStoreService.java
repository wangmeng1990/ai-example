package com.wm.ai.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@AllArgsConstructor
public class VectorStoreService {
    private final VectorStore vectorStore;
    private final DocumentService documentService;

    /**
     * 添加文本信息
     * @return
     */
    public String userPortraits() {
        List<Document> documents = documentService.loadUserPortraits();
        vectorStore.add(documents);
        return "ok";
    }
}
