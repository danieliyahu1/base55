package com.akatsuki.base55.agent;

import com.akatsuki.base55.domain.AiResponseDomain;
import com.akatsuki.base55.domain.SubTask;
import com.akatsuki.base55.domain.agent.SubTaskExecutorResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;

import static com.akatsuki.base55.constant.AgentConstants.SYSTEM_PROMPT_SUB_TASK_EXECUTOR;
import static com.akatsuki.base55.constant.AgentConstants.USER_PROMPT_SUB_TASK_EXECUTOR;

public class SubTaskExecutor {
    private final ChatClient chatClient;

    public SubTaskExecutor (@Qualifier("executorChatClient") ChatModel chatModel,
                            ToolCallbackProvider toolCallbackProvider){
        this.chatClient = ChatClient.builder(chatModel)
                .defaultToolCallbacks(toolCallbackProvider)
                .build();
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

}
