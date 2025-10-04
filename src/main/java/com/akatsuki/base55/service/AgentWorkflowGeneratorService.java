package com.akatsuki.base55.service;

import com.akatsuki.base55.domain.workflow.UserPromptIntent;
import com.akatsuki.base55.domain.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.akatsuki.base55.constant.PlatformConstants.*;

@Slf4j
@Service
public class AgentWorkflowGeneratorService {
    private final ChatClient groqChatClient;
//switch back to reasoningChatClient
    public AgentWorkflowGeneratorService(@Qualifier("executorChatClient") ChatClient groqChatClient) {
        this.groqChatClient = groqChatClient;
    }

    public Workflow generateAgentWorkflow(String task){
        UserPromptIntent userIntent = userIntentParsing(task);
        log.info("User Intent: Primary Goal - {}, Secondary Goals - {}", userIntent.primaryGoal(), userIntent.secondaryGoals());
        return this.groqChatClient.prompt()
                .system(s -> s.text(WORKFLOW_GENERATION_ROLE))
                .user(u -> u.text(WORKFLOW_GENERATION_PROMPT)
                        .param("primaryGoal", userIntent.primaryGoal())
                        .param("secondaryGoals", String.join(", ", userIntent.secondaryGoals())))
                .call()
                .entity(Workflow.class);
    }

    private UserPromptIntent userIntentParsing(String task){
        return this.groqChatClient.prompt()
                .system(s -> s.text(USER_INTENT_ROLE))
                .user(u -> u.text(USER_INTENT_TASK_DESCRIPTION)
                        .param("task", task))
                .call()
                .entity(UserPromptIntent.class);
    }
}
