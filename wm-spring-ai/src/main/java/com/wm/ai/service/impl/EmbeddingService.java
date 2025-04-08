package com.wm.ai.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@AllArgsConstructor
public class EmbeddingService {
    private final EmbeddingModel embeddingModel;

    private final DocumentService documentService;

    /**
     * 嵌入文本
     * @param text
     * @return
     */
    public float[] embed1(String text) {
        return embeddingModel.embed(text);
    }

    /**
     * 嵌入文件
     * @param file
     */
    public void embed2(MultipartFile file) {
        List<Document> documents = documentService.document1(file);
        for (Document document : documents){
            embeddingModel.embed(document);
        }
    }
}
