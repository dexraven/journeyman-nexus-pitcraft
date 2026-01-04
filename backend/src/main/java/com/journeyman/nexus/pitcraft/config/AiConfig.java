package com.journeyman.nexus.pitcraft.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    // Inject the key directly (even if hardcoded in properties, this will grab it)
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Bean
    public ChatModel chatModel() {
        System.out.println("🔥 MANUALLY CREATING CHAT MODEL WITH KEY: " + apiKey.substring(0, 5) + "...");

        // 1. Create the Low-Level API Connection
        OpenAiApi openAiApi = new OpenAiApi(apiKey);

        // 2. Create the High-Level Model (The "Brain")
        return new OpenAiChatModel(openAiApi);
    }
}