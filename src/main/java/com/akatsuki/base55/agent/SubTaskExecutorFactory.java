package com.akatsuki.base55.agent;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SubTaskExecutorFactory {

    private final @Qualifier("openAiChatModel") ChatModel chatModel;

    public SubTaskExecutorFactory(@Qualifier("openAiChatModel") ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public SubTaskExecutor create(ToolCallbackProvider toolCallbackProvider) {
        return new SubTaskExecutor(chatModel, toolCallbackProvider);
    }
}
