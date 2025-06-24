package com.wm.chat;

import com.alibaba.cloud.ai.analyticdb.AnalyticDbVectorStore;
import com.alibaba.cloud.ai.analyticdb.AnalyticDbVectorStoreProperties;
import com.aliyun.gpdb20160503.Client;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages ={"com.wm.chat","com.alibaba.cloud.ai"} )
@AutoConfiguration
@ConditionalOnClass({ EmbeddingModel.class, Client.class, AnalyticDbVectorStore.class })
@EnableConfigurationProperties({ AnalyticDbVectorStoreProperties.class })
@ConditionalOnProperty(prefix = "spring.ai.vectorstore.analytic", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }

}
