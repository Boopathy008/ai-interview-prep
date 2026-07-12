package com.interviewprep.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI configuration for the Gemini Developer API.
 *
 * Spring Boot automatically creates the ChatModel using:
 * spring.ai.google.genai.api-key
 * spring.ai.google.genai.chat.options.*
 */
@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}