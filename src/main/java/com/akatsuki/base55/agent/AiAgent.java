package com.akatsuki.base55.agent;

import com.akatsuki.base55.domain.agent.AiAgentMetadata;
import com.akatsuki.base55.domain.agent.LlmEvaluationResult;
import com.akatsuki.base55.domain.agent.TaskExecutorResponse;
import com.akatsuki.base55.enums.TaskOutcome;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;

import java.util.List;
import java.util.UUID;

@Slf4j
public class AiAgent {

    @Getter
    private final UUID agentId;
    @Getter
    private final String agentDescription;
    private final TaskExecutor taskExecutor;
    private final ResultEvaluator resultEvaluator;

    public AiAgent(AiAgentMetadata aiAgentMetadata, TaskExecutor subTaskExecutor, ResultEvaluator resultEvaluator) {
        this.agentId = aiAgentMetadata.id();
        this.agentDescription = aiAgentMetadata.description();
        this.taskExecutor = subTaskExecutor;
        this.resultEvaluator = resultEvaluator;
    }

    public TaskExecutorResponse executeTask(String task) {
        log.info("Before agentic loop for agent: {}", agentId);
        return agenticLoop(task);
    }

    private TaskExecutorResponse agenticLoop(String task){
        log.info("Starting agentic loop for agent: {} with task: {}", agentId, task);
        LlmEvaluationResult llmEvaluationResult = null;
        TaskExecutorResponse aiResponseDomain = null;
        while(llmEvaluationResult == null || llmEvaluationResult.result() == TaskOutcome.RETRY){
            aiResponseDomain = runTaskExecutor(task);
            log.info("Evaluating task response for agent: {} with task: {}", agentId, task);
            llmEvaluationResult = evaluateSubTaskResponse(task, aiResponseDomain, taskExecutor.getConversationHistory(agentId));
            log.info("Evaluation response for agent {} - Reason: {}, Result: {}", agentId, llmEvaluationResult.reason(), llmEvaluationResult.result());
        }
        log.info("Agentic loop completed for agent: {}", agentId);
        if(llmEvaluationResult.result() == TaskOutcome.COMPLETED){
            log.info("Task completed successfully for agent: {}", agentId);
            log.info("Clearing chat memory for agent: {}", agentId);
            taskExecutor.clearChatMemory(agentId.toString());
        }
        return aiResponseDomain;
    }

    private TaskExecutorResponse runTaskExecutor(String task) {
        return taskExecutor.executeTask(
                task,
                agentId.toString()
        );
    }

    private LlmEvaluationResult evaluateSubTaskResponse(String task, TaskExecutorResponse subTaskExecutorResponse, List<Message> conversationHistory) {
        return resultEvaluator.evaluationSubTaskResponse(
                task,
                subTaskExecutorResponse.response(),
                conversationHistory
        );
    }
}