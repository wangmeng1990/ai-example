package com.wm.ai.conf;

import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageChatMemoryConf {

    /**
     * 默认基于内存的ChatMemory实现
     * @return
     */
    @Bean
    MessageWindowChatMemory messageWindowChatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(25)
                .build();
    }
}
