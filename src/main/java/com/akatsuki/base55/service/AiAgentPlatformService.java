package com.akatsuki.base55.service;

import com.akatsuki.base55.agent.AiAgent;
import com.akatsuki.base55.domain.agent.AiAgentConfig;
import com.akatsuki.base55.domain.agent.AiAgentMetadata;
import com.akatsuki.base55.domain.mcp.tools.McpToolSpec;
import com.akatsuki.base55.domain.workflow.Workflow;
import com.akatsuki.base55.domain.workflow.step.WorkflowStep;
import com.akatsuki.base55.exception.ToolNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.akatsuki.base55.constant.PlatformConstants.SIMILARITY_SEARCH_TOP_K;

@Slf4j
@Component
public class AiAgentPlatformService {

    private final AgentWorkflowGeneratorService agentWorkflowGeneratorService;
    private final McpToolFilteringService mcpToolFilteringService;
    private final AiAgentService aiAgentService;
    private final ToolService toolService;

    public AiAgentPlatformService(AgentWorkflowGeneratorService agentWorkflowGeneratorService, AiAgentService aiAgentService,
                         McpToolFilteringService mcpToolFilteringService, ToolService agentToolService) {
        this.agentWorkflowGeneratorService = agentWorkflowGeneratorService;
        this.mcpToolFilteringService = mcpToolFilteringService;
        this.aiAgentService = aiAgentService;
        this.toolService = agentToolService;
    }

    public Map<String, Object> createAgent(String task) throws ToolNotFoundException {
        log.info("Creating agent for task: {}", task);
        Workflow workflow = agentWorkflowGeneratorService.generateAgentWorkflow(task);
        log.info("Generated workflow: {}", workflow);
        List<McpToolSpec> mcpToolSpecs = getFilteredTools(workflow);
        AiAgentConfig aiAgentConfig = new AiAgentConfig(createAgentMetadata(workflow.aiAgentDescription(), workflow.systemPrompt()), mcpToolSpecs);

        AiAgent agent =  aiAgentService.createAgent(
                aiAgentConfig
        );
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("agent", agent);
        responseMap.put("Tools", mcpToolSpecs);
        return responseMap;
    }

    public Workflow generateAgentWorkflow(String task) {
        return agentWorkflowGeneratorService.generateAgentWorkflow(task);
    }

    public List<McpToolSpec> getFilteredTools(String task) throws ToolNotFoundException {
        return this.getFilteredTools(
                this.agentWorkflowGeneratorService.generateAgentWorkflow(task)
                );
    }

    private AiAgentMetadata createAgentMetadata(String description, String systemPrompt){
        return new AiAgentMetadata(java.util.UUID.randomUUID(), description, systemPrompt);
    }

    private List<McpToolSpec> getFilteredTools(Workflow workflow) throws ToolNotFoundException {
        Set<McpToolSpec> filteredMcpToolSpecsForStep = new HashSet<>();
        Set<McpToolSpec> filteredMcpToolSpecsForWorkflow = new HashSet<>();
        log.info("Attempting filtering tools for workflow with {} steps", workflow.WorkflowSteps().size());
        for(WorkflowStep step : workflow.WorkflowSteps()){
            String similaritySearchQuery = getQueryForSimilaritySearch(step);
            filteredMcpToolSpecsForStep.addAll(toolService.getSimilarToolsByQueryAndTopK(similaritySearchQuery, SIMILARITY_SEARCH_TOP_K));
            filteredMcpToolSpecsForWorkflow.addAll(filterToolsForWorkflowStep(step, filteredMcpToolSpecsForStep.stream().toList()));
        }
        log.info("Successfully filtered tools for workflow. Total filtered tools: {}", filteredMcpToolSpecsForWorkflow.size());
        return filteredMcpToolSpecsForWorkflow.stream().toList();
    }

    private List<McpToolSpec> filterToolsForWorkflowStep(WorkflowStep workflowStep, List<McpToolSpec> mcpToolSpecs) throws ToolNotFoundException {
        return this.mcpToolFilteringService.getListOfToolsForWorkflowStep(workflowStep, mcpToolSpecs);
    }

    private String getQueryForSimilaritySearch(WorkflowStep step){
        return String.format(
                "Task: %s. Required inputs: %s.",
                step.task(),
                step.requiredData().toString()
        );
    }
}