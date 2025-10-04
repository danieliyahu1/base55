package com.akatsuki.base55.agent;

import com.akatsuki.base55.domain.LlmEvaluationResult;
import com.akatsuki.base55.domain.SubTask;
import com.akatsuki.base55.domain.agent.SubTaskExecutorResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static com.akatsuki.base55.constant.AgentConstants.SYSTEM_PROMPT_SUB_TASK_EVALUATOR;
import static com.akatsuki.base55.constant.AgentConstants.USER_PROMPT_SUB_TASK_EVALUATOR;

@Component
public class ResultEvaluator {

    private final ChatClient chatClient;

    public ResultEvaluator (@Qualifier("evaluationChatClient") ChatClient chatClient){
        this.chatClient = chatClient;
    }

    public LlmEvaluationResult evaluationSubTaskResponse(String task, SubTask subTask, SubTaskExecutorResponse subTaskExecutorResponse){
        return this.chatClient.prompt()
                .system(s -> s.text(SYSTEM_PROMPT_SUB_TASK_EVALUATOR))
                .user(u -> u.text(USER_PROMPT_SUB_TASK_EVALUATOR)
                        .param("task", task)
                        .param("sub-task", subTask.description())
                        .param("llm-response", subTaskExecutorResponse))
                .call()
                .entity(LlmEvaluationResult.class);
    }
}
