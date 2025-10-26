package com.akatsuki.base55.agent;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TaskExecutorFactory {

    private final ChatModel chatModel;
    private final ChatMemory chatMemory;
    public TaskExecutorFactory(@Qualifier("openAiChatModel") ChatModel chatModel,
                               @Qualifier("executorChatMemory") ChatMemory chatMemory) {
        this.chatModel = chatModel;
        this.chatMemory = chatMemory;
    }

    public TaskExecutor create(ToolCallbackProvider toolCallbackProvider, String agentSystemPrompt) {
        return new TaskExecutor(chatModel, toolCallbackProvider, chatMemory, agentSystemPrompt);
    }
}
