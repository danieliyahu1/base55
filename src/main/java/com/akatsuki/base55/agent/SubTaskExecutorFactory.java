package com.akatsuki.base55.agent;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class SubTaskExecutorFactory {

    private final @Qualifier("openAiChatModel") ChatModel chatModel;

    public SubTaskExecutorFactory(@Qualifier("openAiChatModel") ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public SubTaskExecutor create(ToolCallback[] toolCallbacks) {
        return new SubTaskExecutor(chatModel, createToolCallbackProviderFromCallbackTools(toolCallbacks));
    }

    private ToolCallbackProvider createToolCallbackProviderFromCallbackTools(ToolCallback[] toolCallbacks){
        return ToolCallbackProvider.from(Arrays.stream(toolCallbacks).toList());
    }
}
