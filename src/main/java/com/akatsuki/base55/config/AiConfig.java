package com.akatsuki.base55.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
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
public class AiConfig {

    @Bean
    public ChatClient groqChatClient(
            @Qualifier("openAiChatModel") ChatModel openAiChatModel,
            ToolCallbackProvider toolCallbackProvider) {

        log.info("Registering OpenAI ChatClient with {} tool callbacks",
                toolCallbackProvider.getToolCallbacks().length);

        return ChatClient.builder(openAiChatModel)
                //.defaultToolCallbacks(toolCallbackProvider)
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
    public ChatClient openRouterChatClient(
            @Qualifier("openRouterChatModel") ChatModel openRouterChatModel) {

        return ChatClient.builder(openRouterChatModel)
                .build();
    }

}
