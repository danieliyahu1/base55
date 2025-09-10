package com.akatsuki.base55.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;

public class SubTaskExecutor {
    private final ChatClient chatClient;

    public SubTaskExecutor (@Qualifier("openAiChatModel") ChatModel chatModel,
                            ToolCallbackProvider toolCallbackProvider){
        this.chatClient = ChatClient.builder(chatModel)
                .defaultToolCallbacks(toolCallbackProvider)
                .build();
    }
}
