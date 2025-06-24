package com.wm.ai.service.impl;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.ai.model.transformer.SummaryMetadataEnricher;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.*;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentService {

    private final ResourcePatternResolver resolver;
    private final ChatModel chatModel;

    public DocumentService(ResourcePatternResolver resolver,ChatModel chatModel) {
        this.resolver = resolver;
        this.chatModel = chatModel;
    }

    /**
     * ETL:提取关键词和摘要
     * @return
     */
    public List<Document> loadUserPortraits() {
        Resource resource = resolver.getResource("classpath:rag/用户画像.md");
        //分割
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter(50,10,1,500,true);
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);

        List<Document> read = tikaDocumentReader.read();
        List<Document> apply = tokenTextSplitter.apply(read);
        List<Document> documents = new ArrayList<>(apply);

        //设置元数据关联用户
        documents.forEach(document -> {
            document.getMetadata().put("userId",1);
        });

        //提取关键字：用户检索匹配
        KeywordMetadataEnricher keywordMetadataEnricher=new KeywordMetadataEnricher(chatModel,3);
        keywordMetadataEnricher.apply(documents);

        //提取摘要
        String DEFAULT_SUMMARY_EXTRACT_TEMPLATE = """
        以下是内容:
        {context_str}

        总结以上内容的关键主题，
        尽量保持简短精确

        摘要:""";
        SummaryMetadataEnricher summaryMetadataEnricher=new SummaryMetadataEnricher(chatModel,List.of(SummaryMetadataEnricher.SummaryType.CURRENT),DEFAULT_SUMMARY_EXTRACT_TEMPLATE, MetadataMode.EMBED);
        summaryMetadataEnricher.apply(documents);
        return documents;
    }
}