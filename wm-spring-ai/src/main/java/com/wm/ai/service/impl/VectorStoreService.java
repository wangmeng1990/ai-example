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
     * @param text
     * @return
     */
    public String store1(String text) {
        List<Document> documents = documentService.document4(text);
        vectorStore.add(documents);
        return "ok";
    }

    /**
     * 添加文件信息
     * @param file
     * @return
     */
    public String store2(MultipartFile file) {
        List<Document> documents = documentService.document1(file);
        vectorStore.add(documents);
        return "ok";
    }

    /**
     * 检索相似信息
     * @param userInput
     * @return
     */
    public List<Document> store3(String userInput) {
        List<Document> documents = vectorStore.similaritySearch(userInput);
        return documents;
    }
}
