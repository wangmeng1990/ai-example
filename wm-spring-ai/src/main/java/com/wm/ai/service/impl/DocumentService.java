package com.wm.ai.service.impl;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@AllArgsConstructor
public class DocumentService {

    @SneakyThrows
    public List<Document> document1(MultipartFile file) {
        Resource resource = new InputStreamResource(file.getInputStream());
        //读取
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
        List<Document> read = tikaDocumentReader.read();
        //分割
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        List<Document> apply = tokenTextSplitter.apply(read);

        // ContentFormatter contentFormatter=DefaultContentFormatter.builder().build();
        // ContentFormatTransformer contentFormatTransformer = new ContentFormatTransformer(contentFormatter);
        // contentFormatTransformer.apply(apply);
        return apply;
    }

    public List<Document> document2(String path) {
        Resource resource = new FileSystemResource(path);
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
        return tikaDocumentReader.read();
    }

    /**
     * 网络资源
     *
     * @param url
     * @return
     */
    @SneakyThrows
    public List<Document> document3(String url) {
        Resource resource = new UrlResource(url);
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
        return tikaDocumentReader.read();
    }

    /**
     * 文本内容直接转化为Document
     *
     * @param context
     * @return
     */
    public List<Document> document4(String context) {
        return List.of(new Document(context));
    }
}