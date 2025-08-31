package com.akatsuki.base55.service;

import com.akatsuki.base55.domain.UserPromptIntent;
import com.akatsuki.base55.domain.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.akatsuki.base55.constant.orchestrationConstants.GENERATE_WORKFLOW_PROMPT;

@Slf4j
@Service
public class AgentTasksGeneratorService {
    private final ChatClient groqChatClient;

    public AgentTasksGeneratorService(@Qualifier("groqChatClient") ChatClient groqChatClient) {
        this.groqChatClient = groqChatClient;
    }

    public Workflow generateAgentTasks(String task){
        UserPromptIntent userIntent = userIntentParsing(task);
        log.info("User Intent: Primary Goal - {}, Secondary Goals - {}", userIntent.primaryGoal(), userIntent.secondaryGoals());
        return this.groqChatClient.prompt()
                .user(u -> u.text(GENERATE_WORKFLOW_PROMPT)
                        .param("primaryGoal", userIntent.primaryGoal())
                        .param("secondaryGoals", String.join(", ", userIntent.secondaryGoals())))
                .call()
                .entity(Workflow.class);
    }

    private UserPromptIntent userIntentParsing(String task){
        return this.groqChatClient.prompt()
                .user(u -> u.text("understand the user’s request. Extract the primary goal of the (what outcome the agent must achieve) and secondary goals that the ai agent should have in order to be able to execute the task. : {task}")
                        .param("task", task))
                .call()
                .entity(UserPromptIntent.class);
    }
}
