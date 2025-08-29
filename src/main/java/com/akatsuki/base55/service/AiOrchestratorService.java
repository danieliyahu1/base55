package com.akatsuki.base55.service;

import com.akatsuki.base55.domain.orchestrator.OrchestratorNextStepRequest;
import com.akatsuki.base55.domain.orchestrator.OrchestratorNextStepResponse;
import com.akatsuki.base55.domain.workflow.*;
import com.akatsuki.base55.domain.workflow.step.WorkflowStep;
import com.akatsuki.base55.domain.workflow.step.WorkflowStepRequest;
import com.akatsuki.base55.domain.workflow.step.WorkflowStepResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

import static com.akatsuki.base55.constant.orchestrationConstants.*;

@Slf4j
@Service
public class AiOrchestratorService {
    private final ChatClient groqChatClient;
    private final ChatClient huggingfaceChatClient;

    public AiOrchestratorService(@Qualifier("openAiChatClient") ChatClient groqChatClient,
                                 @Qualifier("huggingFaceChatClient") ChatClient huggingfaceChatClient) {
        this.groqChatClient = groqChatClient;
        this.huggingfaceChatClient = huggingfaceChatClient;
    }

    public WorkflowStepResponse executeTask(String task) {
        // Implement orchestration logic here
        log.info("Starting orchestration for task: {}", task);
        Workflow workflow = generateWorkflow(task);
        log.info("Generated workflow: {} \n analysis: {}", workflow.WorkflowSteps(), workflow.analysis());
        return executeWorkflow(task, workflow);
    }

    private Workflow generateWorkflow(String prompt){
        return this.huggingfaceChatClient.prompt()
                .user(u -> u.text(WORKFLOW_PROMPT)
                        .param("task", prompt))
                .call()
                .entity(Workflow.class);
    }

    private WorkflowStepResponse executeWorkflow(String task, Workflow workflow){
        Map<String, WorkflowStep> stepMap = workflow.WorkflowSteps().stream()
                .collect(java.util.stream.Collectors.toMap(WorkflowStep::id, step -> step));

        Map<String, String> stepResults = new java.util.HashMap<>();

        WorkflowStep currentStep = workflow.WorkflowSteps().getFirst();
        WorkflowStepResponse stepResponse = null;

        while (currentStep != null) {
            Map<String, String> dependenciesResults = creteDpendenciesResults(currentStep, stepResults);

            stepResponse = executeStep(createWorkflowStepRequest(task, currentStep, dependenciesResults));
            log.info("Executed step: {} with result: {}", currentStep.id(), stepResponse.result());
            stepResults.put(currentStep.id(), stepResponse.result());
            currentStep = determineNextSetp(stepResponse, currentStep, stepMap, task);
        }

        return stepResponse;
    }

    private WorkflowStep determineNextSetp(WorkflowStepResponse stepResponse, WorkflowStep currentStep, Map<String, WorkflowStep> stepMap, String originalTask) {
        Map<String, String> nextSteps = currentStep.nextSteps();

        if (nextSteps.isEmpty()) return null;
        if (nextSteps.size() == 1) {
            return stepMap.get(nextSteps.keySet().iterator().next());
        }

        OrchestratorNextStepRequest orchestratorNextStepRequest = new OrchestratorNextStepRequest(
                originalTask,
                currentStep.description(),
                stepResponse.result(),
                nextSteps
        );

        OrchestratorNextStepResponse chosenStepId = this.huggingfaceChatClient.prompt()
                .user(u -> u.text(DETERMINE_NEXT_STEP_PROMPT)
                        .param("OrchestratorNextStepRequest", orchestratorNextStepRequest))
                .call()
                .entity(OrchestratorNextStepResponse.class);
        log.info("Chosen next step: {} because {}", chosenStepId.id(), chosenStepId.reason());
        return stepMap.get(chosenStepId.id());
    }


    private Map<String, String> creteDpendenciesResults(WorkflowStep currentStep, Map<String, String> stepResults) {
        return currentStep.previousStepResultDependencies().stream()
                .filter(stepResults::containsKey)
                .collect(Collectors.toMap(
                        dependencyId -> dependencyId,
                        dependencyId -> stepResults.get(dependencyId)
                ));
    }

    private WorkflowStepResponse executeStep(WorkflowStepRequest workflowStepRequest) {
        return this.groqChatClient.prompt()
                .user(u -> u.text(WORKFLOW_STEP_PROMPT)
                        .param("workflowStepRequest", workflowStepRequest))
                .call()
                .entity(WorkflowStepResponse.class);
    }

    private WorkflowStepRequest createWorkflowStepRequest(String task, WorkflowStep currentStep, Map<String,String> dependenciesResults) {
        return new WorkflowStepRequest(task, currentStep.description(), dependenciesResults);
    }
}
