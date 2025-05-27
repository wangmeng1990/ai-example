package com.wm.ai.service.impl;

import lombok.SneakyThrows;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.ContentFormatter;
import org.springframework.ai.document.DefaultContentFormatter;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.ai.transformer.SummaryMetadataEnricher;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.*;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    private boolean docLoaded=false;

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

    public List<Document> document5()  {
        List<Document> documents = new ArrayList<>();
        if (docLoaded) {
            return documents;
        }
        Resource[] resources = null;
        try {
            resources = resolver.getResources("classpath:doc/*.md");
            docLoaded=true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (null!=resources&&resources.length>0) {
            for (Resource resource : resources) {
                TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
                documents.addAll(tikaDocumentReader.read());
            }
        }
        return documents;
    }

    /**
     * ETL:提取关键词和摘要
     * @return
     */
    public List<Document> document6() {
        List<Document> documents = new ArrayList<>();
        Resource[] resources = null;
        try {
            resources = resolver.getResources("classpath:doc/*.md");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (null!=resources&&resources.length>0) {
            //分割
            TokenTextSplitter tokenTextSplitter = new TokenTextSplitter(100,30,5,500,true);
            for (Resource resource : resources) {
                TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);

                List<Document> read = tikaDocumentReader.read();
                List<Document> apply = tokenTextSplitter.apply(read);
                documents.addAll(apply);
            }
            //自定义元数据过滤规则
            ContentFormatter contentFormatter= DefaultContentFormatter.builder()
                    .withExcludedEmbedMetadataKeys("embedding")
                    .build();
            documents.forEach(document -> {
                document.setContentFormatter(contentFormatter);
            });
            //提取关键字
            KeywordMetadataEnricher keywordMetadataEnricher=new KeywordMetadataEnricher(chatModel,3);
            keywordMetadataEnricher.apply(documents);

            //提取摘要
            String DEFAULT_SUMMARY_EXTRACT_TEMPLATE = """
			以下是内容:
			{context_str}

			总结以上内容的关键主题.

			摘要:""";
            SummaryMetadataEnricher summaryMetadataEnricher=new SummaryMetadataEnricher(chatModel,List.of(SummaryMetadataEnricher.SummaryType.CURRENT),DEFAULT_SUMMARY_EXTRACT_TEMPLATE, MetadataMode.EMBED);
            summaryMetadataEnricher.apply(documents);
        }
        return documents;
    }
}