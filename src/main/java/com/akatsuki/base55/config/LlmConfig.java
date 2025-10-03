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

    @Bean
    @Qualifier("executorChatClient")
    public ChatClient openRouterChatClient(
            @Qualifier("openRouterChatModel") ChatModel openRouterChatModel,
            ToolCallbackProvider toolCallbackProvider) {

        log.info("Registering OpenAI ChatClient with {} tool callbacks",
                toolCallbackProvider.getToolCallbacks().length);

        return ChatClient.builder(openRouterChatModel)
                .defaultToolCallbacks(toolCallbackProvider)
                .build();
    }

    @Bean
    public ChatClient deepSeekChatClient(
            @Qualifier("deepSeekChatModel") ChatModel deepseekChatModel) {
        return ChatClient.builder(deepseekChatModel)
                .build();
    }

    @Bean
    public ChatModel huggingFaceChatModel(
            @Value("${huggingface.chat.api-key}") String apiKey,
            @Value("${huggingface.chat.url}") String baseUrl,
            @Value("${huggingface.chat.model}") String model,
            @Value("${huggingface.chat.temperature}") double temperature) {

        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl) // https://router.huggingface.co/v1
                .build();

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(model) // e.g. deepseek-ai/DeepSeek-V3.2-Exp:novita
                .temperature(temperature)
                .build();

        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(options)
                .build();
    }

    @Bean
    @Qualifier("reasoningChatClient")
    public ChatClient huggingFaceChatClient(
            @Qualifier("huggingFaceChatModel") ChatModel huggingFaceChatModel, JdbcChatMemoryRepository chatMemoryRepository) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(10)
                .build();

        return ChatClient
                .builder(huggingFaceChatModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }
}
