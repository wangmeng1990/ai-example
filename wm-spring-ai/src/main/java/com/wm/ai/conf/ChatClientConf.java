package com.wm.ai.conf;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConf {

    /**
     * 使用application-dev.yml配置的model自动装配的ChatModel创建ChatClient
     * @param builder
     * @return
     */
    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder
            .defaultSystem("你是一个智能机器人，可以进行问答")
            //输出请求，响应日志
            .defaultAdvisors(new SimpleLoggerAdvisor())
            .build();
    }

    /**
     * 默认基于内存的ChatMemory实现
     * @return
     */
    @Bean
    MessageWindowChatMemory messageWindowChatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(100)
                .build();
    }
}
