package com.akatsuki.base55.agent;

import com.akatsuki.base55.domain.agent.LlmEvaluationResult;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.akatsuki.base55.constant.AgentConstants.SYSTEM_PROMPT_SUB_TASK_EVALUATOR;
import static com.akatsuki.base55.constant.AgentConstants.USER_PROMPT_TASK_EVALUATOR;

@Component
public class ResultEvaluator {

    private final ChatClient chatClient;
    public ResultEvaluator (@Qualifier("evaluationChatClient") ChatClient chatClient){
        this.chatClient = chatClient;
    }

    public LlmEvaluationResult evaluationSubTaskResponse(String task, String taskResponse, List<Message> conversationHistory) {
        return this.chatClient.prompt()
                .system(s -> s.text(SYSTEM_PROMPT_SUB_TASK_EVALUATOR))
                .user(u -> u.text(USER_PROMPT_TASK_EVALUATOR)
                        .param("task", task)
                        .param("llm-response", taskResponse))
                .call()
                .entity(LlmEvaluationResult.class);
    }
}
