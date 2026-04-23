package com.smart.smartcontactmanager.config;

import org.springframework.ai.chat.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    /**
     * Spring AI 0.8.x uses org.springframework.ai.chat.ChatClient
     * (NOT ChatClient.Builder — that's the 1.x API)
     * The OpenAI auto-configure creates an OpenAiChatClient bean automatically.
     * We just alias it here so the controller can @Autowired ChatClient.
     */
    @Bean
    public ChatClient chatClient(ChatClient openAiChatClient) {
        return openAiChatClient;
    }
}