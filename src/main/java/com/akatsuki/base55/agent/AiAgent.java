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

    public AiResponseDomain executeTask2(String task) {
        log.info("Starting task execution for agent: {}", agentId);

        log.info("Entering agentic loop for agent: {}", agentId);
        return agenticLoop(task);
    }

    // need to change the result in AiResponseDomain to be accurate
    private SubTaskExecutorResponse agenticLoop(SubTask firstSubTask, String originalTask){
        SubTask currentSubTask = firstSubTask;
        SubTaskExecutorResponse subTaskExecutorResponse = null;
        LlmEvaluationResult llmEvaluationResult = null;
        log.info("Starting agentic loop for agent: {}", agentId);
        while(true){
            log.info("Current SubTask for agent {}: {}", agentId, currentSubTask.description());
            subTaskExecutorResponse = executeSubTask(currentSubTask);
            log.info("SubTask executed for agent {}: {}", agentId, subTaskExecutorResponse);
            log.info("Evaluating SubTask response for agent: {}", agentId);
            llmEvaluationResult = evaluateSubTaskResponse(originalTask, currentSubTask, subTaskExecutorResponse);
            log.info("Evaluation result for agent {} - Reason: {}, Result: {}", agentId, llmEvaluationResult.reason(), llmEvaluationResult.result());
            log.info("Generating next SubTask for agent: {}", agentId);
            if(llmEvaluationResult.result() == AgentFlowControl.TASK_COMPLETED){
                log.info("Task completed for agent: {}", agentId);
                break;
            }
            currentSubTask = taskDecomposer.getNextSubTask(currentSubTask, generateSystemMessageForDecomposeTask(originalTask), agentId.toString(), llmEvaluationResult);
            log.info("Next SubTask generated for agent: {}", agentId);
        }
        log.info("Agentic loop completed for agent: {}", agentId);
        return subTaskExecutorResponse;
    }

    private AiResponseDomain agenticLoop(String task){
        LlmEvaluationResult llmEvaluationResult = null;
        AiResponseDomain aiResponseDomain = null;
        log.info("Starting agentic loop for agent: {} with task: {}", agentId, task);
        while(llmEvaluationResult == null || llmEvaluationResult.result() != AgentFlowControl.TASK_COMPLETED){
            aiResponseDomain = executeSubTask(task);
            log.info("Evaluating task response for agent: {} with task: {}", agentId, task);
            llmEvaluationResult = evaluateSubTaskResponse(task, aiResponseDomain);
            log.info("Evaluation response for agent {} - Reason: {}, Result: {}", agentId, llmEvaluationResult.reason(), llmEvaluationResult.result());

        }
        log.info("Agentic loop completed for agent: {}", agentId);
        return aiResponseDomain;
    }

    private SubTaskExecutorResponse executeSubTask(SubTask subTask) {
        return subTaskExecutor.executeSubTask(
                subTask,
                agentId.toString()
        );
    }

    private AiResponseDomain executeSubTask(String task) {
        return subTaskExecutor.executeSubTask(
                task,
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

    private LlmEvaluationResult evaluateSubTaskResponse(String task, AiResponseDomain subTaskExecutorResponse) {
        return resultEvaluator.evaluationSubTaskResponse(
                task,
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