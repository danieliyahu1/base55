package com.akatsuki.base55.agent;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SubTaskExecutorFactory {

    private final ChatModel chatModel;
    private final ChatMemory chatMemory;
    public SubTaskExecutorFactory(@Qualifier("openAiChatModel") ChatModel chatModel,
                                  @Qualifier("executorChatMemory") ChatMemory chatMemory) {
        this.chatModel = chatModel;
        this.chatMemory = chatMemory;
    }

    public SubTaskExecutor create(ToolCallbackProvider toolCallbackProvider){
        return new SubTaskExecutor(chatModel, toolCallbackProvider, chatMemory);
    }
}
