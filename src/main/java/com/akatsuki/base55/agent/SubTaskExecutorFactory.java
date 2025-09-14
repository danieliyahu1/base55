package com.akatsuki.base55.agent;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SubTaskExecutorFactory {

    private final @Qualifier("openAiChatModel") ChatModel chatModel;

    public SubTaskExecutorFactory(@Qualifier("openAiChatModel") ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public SubTaskExecutor create(List<ToolCallback> toolCallbacks) {
        return new SubTaskExecutor(chatModel, createToolCallbackProviderFromCallbackTools(toolCallbacks));
    }

    private ToolCallbackProvider createToolCallbackProviderFromCallbackTools(List<ToolCallback> toolCallbacks){
        return ToolCallbackProvider.from(toolCallbacks);
    }
}
