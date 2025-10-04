package com.akatsuki.base55.agent;

import com.akatsuki.base55.domain.AiResponseDomain;
import com.akatsuki.base55.domain.LlmEvaluationResult;
import com.akatsuki.base55.domain.SubTask;
import com.akatsuki.base55.domain.agent.AiAgentMetadata;
import com.akatsuki.base55.domain.agent.SubTaskExecutorResponse;
import com.akatsuki.base55.enums.AgentFlowControl;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class AiAgent {

    @Getter
    private final UUID agentId;
    private final String systemPrompt;
    @Getter
    private final String agentDescription;
    private final TaskDecomposer taskDecomposer;
    private final SubTaskExecutor subTaskExecutor;
    private final ResultEvaluator resultEvaluator;

    public AiAgent(AiAgentMetadata aiAgentMetadata, TaskDecomposer taskDecomposer, SubTaskExecutor subTaskExecutor, ResultEvaluator resultEvaluator) {
        this.agentId = aiAgentMetadata.id();
        this.systemPrompt = aiAgentMetadata.systemPrompt();
        this.agentDescription = aiAgentMetadata.description();
        this.taskDecomposer = taskDecomposer;
        this.subTaskExecutor = subTaskExecutor;
        this.resultEvaluator = resultEvaluator;
    }

//    public SubTask decomposeTask(String task) {
//        return taskDecomposer.getNextSubTask(task,
//                generateSystemMessageForDecompposeTask(task)
//                , agentId.toString());
//    }

    public SubTaskExecutorResponse executeTask(String task) {
        log.info("Starting task execution for agent: {}", agentId);
        log.info("Generating first SubTask for agent: {}", agentId);
        SubTask firstSubTask = taskDecomposer.firstDecomposition(
                generateSystemMessageForDecomposeTask(task)
                , agentId.toString());
        log.info("First SubTask generated for agent {}: {}", agentId, firstSubTask.description());
        log.info("Entering agentic loop for agent: {}", agentId);
        return agenticLoop(firstSubTask, task);
    }

    // need to change the result in AiResponseDomain to be accurate
    private SubTaskExecutorResponse agenticLoop(SubTask firstSubTask, String originalTask){
        SubTask currentSubTask = firstSubTask;
        SubTaskExecutorResponse subTaskExecutorResponse = null;
        LlmEvaluationResult llmEvaluationResult = null;
        log.info("Starting agentic loop for agent: {}", agentId);
        while(llmEvaluationResult == null || llmEvaluationResult.result() != AgentFlowControl.TASK_COMPLETED){
            log.info("Current SubTask for agent {}: {}", agentId, currentSubTask.description());
            subTaskExecutorResponse = executeSubTask(currentSubTask);
            log.info("SubTask executed for agent {}: {}", agentId, subTaskExecutorResponse.descriptionOfSubTaskExecution());
            log.info("Evaluating SubTask response for agent: {}", agentId);
            llmEvaluationResult = evaluateSubTaskResponse(originalTask, currentSubTask, subTaskExecutorResponse);
            log.info("Evaluation result for agent {} - Reason: {}, Result: {}", agentId, llmEvaluationResult.reason(), llmEvaluationResult.result());
            log.info("Generating next SubTask for agent: {}", agentId);
            currentSubTask = taskDecomposer.getNextSubTask(currentSubTask, generateSystemMessageForDecomposeTask(originalTask), agentId.toString(), llmEvaluationResult);
            log.info("Next SubTask generated for agent: {}", agentId);
        }
        log.info("Agentic loop completed for agent: {}", agentId);
        return subTaskExecutorResponse;
    }

    private SubTaskExecutorResponse executeSubTask(SubTask subTask) {
        return subTaskExecutor.executeSubTask(
                subTask,
                agentId.toString()
        );
    }

    private LlmEvaluationResult evaluateSubTaskResponse(String originalTask, SubTask subTask, SubTaskExecutorResponse subTaskExecutorResponse) {
        return resultEvaluator.evaluationSubTaskResponse(
                originalTask,
                subTask,
                subTaskExecutorResponse
        );
    }

    private String generateSystemMessageForDecomposeTask(String task) {
        return this.systemPrompt
                + "\n"
                + "The original task from the user: \n"
                + task;
    }
}