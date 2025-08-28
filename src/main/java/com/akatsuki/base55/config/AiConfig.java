package com.akatsuki.base55.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.tool.ToolCallbackProvider;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, ToolCallbackProvider toolCallbackProvider) {
        log.info("toolCallbackProvider: {}",
                Arrays.stream(toolCallbackProvider.getToolCallbacks())
                        .map(tc -> tc.getToolDefinition().name())
                        .collect(Collectors.joining(", ")));
        log.info("toolCallbackProvider count: {}", toolCallbackProvider.getToolCallbacks().length);
        return chatClientBuilder.defaultToolCallbacks(toolCallbackProvider).build();
    }
}
