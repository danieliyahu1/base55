package com.akatsuki.base55.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class LlmConfig {

    @Bean
    @Qualifier("evaluationChatClient")
    public ChatClient groqChatClient(
            @Qualifier("openAiChatModel") ChatModel openAiChatModel) {
        return ChatClient.builder(openAiChatModel)
                .build();
    }

    @Bean
    @Qualifier("executorChatClient")
    public ChatClient openRouterChatClient(
            @Qualifier("openAiChatModel") ChatModel openRouterChatModel,
            ToolCallbackProvider toolCallbackProvider, @Qualifier("executorChatMemory") ChatMemory chatMemory) {
        log.info("Registering OpenAI ChatClient with {} tool callbacks",
                toolCallbackProvider.getToolCallbacks().length);

        return ChatClient.builder(openRouterChatModel)
                .defaultToolCallbacks(toolCallbackProvider)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
            )
                .build();
    }

    @Bean
    public ChatClient openAIChatClient(
            @Qualifier("openAiChatModel") ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .build();
    }

    // --- Chat Memory ---

    @Bean
    @Qualifier("executorChatMemory")
    public ChatMemory executorChatMemory(JdbcChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(30)
                .build();
    }


    // --- Chat Models ---

    @Bean
    public ChatModel groqChatModel(
            @Value("${groq.api.key}") String apiKey,
            @Value("${groq.base.url}") String baseUrl,
            @Value("${groq.model}") String model,
            @Value("${groq.temperature}") double temperature) {

        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .build();

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .build();

        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(options)
                .build();
    }

    @Bean
    public ChatModel openRouterChatModel(
            @Value("${openrouter.api.key}") String apiKey,
            @Value("${openrouter.base.url}") String baseUrl,
            @Value("${openrouter.model}") String model,
            @Value("${openrouter.temperature}") double temperature) {

        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .build();

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .build();

        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(options)
                .build();
    }

}