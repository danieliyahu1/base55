package com.akatsuki.base55.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.huggingface.HuggingfaceChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AiConfig {

    @Bean
    @Qualifier("groqChatClient")
    public ChatClient openAiChatClient(
            @Qualifier("openAiChatModel") ChatModel openAiChatModel,
            ToolCallbackProvider toolCallbackProvider) {

        log.info("Registering OpenAI ChatClient with {} tool callbacks",
                toolCallbackProvider.getToolCallbacks().length);

        return ChatClient.builder(openAiChatModel)
                .defaultToolCallbacks(toolCallbackProvider)
                .build();
    }

    @Bean
    @Qualifier("huggingFaceChatClient")
    public ChatClient huggingFaceChatClient(
            @Qualifier("huggingfaceChatModel") HuggingfaceChatModel huggingfaceChatModel) {
        return ChatClient.builder(huggingfaceChatModel)
                .build();
    }

    @Bean
    public OpenAiEmbeddingModel embeddingModel(@Value("${spring.ai.openai.api-key}") String apiKey) {
        return new OpenAiEmbeddingModel(OpenAiApi.builder().apiKey(apiKey).build());
    }
}
