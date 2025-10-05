package com.akatsuki.base55.agent;

import com.akatsuki.base55.domain.AiResponseDomain;
import com.akatsuki.base55.domain.SubTask;
import com.akatsuki.base55.domain.agent.SubTaskExecutorResponse;
import com.akatsuki.base55.domain.agent.TaskExecutorResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;

import static com.akatsuki.base55.constant.AgentConstants.*;

public class SubTaskExecutor {
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public SubTaskExecutor (ChatModel chatModel,
                            ToolCallbackProvider toolCallbackProvider, ChatMemory chatMemory){
        this.chatClient = ChatClient.builder(chatModel)
                .defaultToolCallbacks(toolCallbackProvider)
                .build();
        this.chatMemory = chatMemory;
    }

    public SubTaskExecutorResponse executeSubTask(SubTask subTask, String conversationId) {
        return this.chatClient.prompt()
                .system(s -> s.text(SYSTEM_PROMPT_SUB_TASK_EXECUTOR))
                .user(u -> u.text(USER_PROMPT_SUB_TASK_EXECUTOR)
                        .param("sub-task", subTask.description()))
                .advisors(a -> a.param("conversationId", conversationId))
                .call()
                .entity(SubTaskExecutorResponse.class);
    }

    public TaskExecutorResponse executeSubTask(String task, String conversationId) {
        return this.chatClient.prompt()
                .system(s -> s.text(SYSTEM_PROMPT_TASK_EXECUTOR))
                .user(u -> u.text(USER_PROMPT_SUB_TASK_EXECUTOR)
                        .param("sub-task", task))
                .advisors(a -> a.param("conversationId", conversationId))
                .call()
                .entity(TaskExecutorResponse.class);
    }

    public void clearChatMemory(String conversationId) {
        chatMemory.clear(conversationId);
    }

}
