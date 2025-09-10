package com.akatsuki.base55.agent;

import com.akatsuki.base55.domain.SubTask;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static com.akatsuki.base55.constant.AgentConstants.SYSTEM_PROMPT_TASK_DECOMPOSER;
import static com.akatsuki.base55.constant.AgentConstants.USER_PROMPT_DECOMPOSE_TASK;

@Component
public class TaskDecomposer {

    ChatClient chatClient;

    public TaskDecomposer(@Qualifier("openRouterChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public SubTask decomposeTask(String task, String currentState) {
        return this.chatClient.prompt()
                .system(s -> s.text(SYSTEM_PROMPT_TASK_DECOMPOSER))
                .user(u -> u.text(USER_PROMPT_DECOMPOSE_TASK)
                        .param("task", task)
                        .param("currentState", currentState))
                .call()
                .entity(SubTask.class);
    }
}
