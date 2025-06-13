package com.wm.ai.conf;

import lombok.AllArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.ai.vectorstore.redis.autoconfigure.RedisVectorStoreAutoConfiguration;
import org.springframework.ai.vectorstore.redis.autoconfigure.RedisVectorStoreProperties;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

// 升级 spring AI 1.0.0 弃用
//@Configuration
//@ConditionalOnClass({RedisVectorStore.class})
//// 使用当前配置，排除RedisVectorStoreAutoConfiguration
//@EnableAutoConfiguration(exclude = {RedisVectorStoreAutoConfiguration.class})
//// 读取RedisStack的配置信息
//@EnableConfigurationProperties({RedisVectorStoreProperties.class})
//@AllArgsConstructor
public class RedisVectorConf {
    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel,
                                   RedisVectorStoreProperties properties,
                                   RedisConnectionDetails redisConnectionDetails) {
        JedisPooled jedisPooled = new JedisPooled(redisConnectionDetails.getStandalone().getHost(),
            redisConnectionDetails.getStandalone().getPort()
            , redisConnectionDetails.getUsername(),
            redisConnectionDetails.getPassword());
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
            .indexName(properties.getIndexName())
            .prefix(properties.getPrefix())
            .initializeSchema(properties.isInitializeSchema())
            .build();
    }

}
