package com.akatsuki.base55.service;

import com.akatsuki.base55.domain.agent.LlmEvaluationResult;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;

public class LlmEvaluationService implements LlmEvaluation {
    private final ChatClient openRouterChatClient;

    public LlmEvaluationService(@Qualifier("openRouterChatClient") ChatClient openRouterChatClient) {
        this.openRouterChatClient = openRouterChatClient;
    }

    public LlmEvaluationResult evaluate(String evaluationPrompt, String responseToEvaluate) {
        return this.openRouterChatClient.prompt()
                .system(s -> s.text("You are an expert evaluator."))
                .user(u -> u.text(evaluationPrompt)
                        .param("response", responseToEvaluate))
                .call()
                .entity(LlmEvaluationResult.class);
    }

}
