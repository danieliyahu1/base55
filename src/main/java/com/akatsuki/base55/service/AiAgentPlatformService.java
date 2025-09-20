package com.akatsuki.base55.service;

import com.akatsuki.base55.agent.AiAgent;
import com.akatsuki.base55.domain.agent.AiAgentConfig;
import com.akatsuki.base55.domain.agent.AiAgentMetadata;
import com.akatsuki.base55.domain.mcp.tools.McpToolSpec;
import com.akatsuki.base55.domain.workflow.Workflow;
import com.akatsuki.base55.exception.ToolNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AiAgentPlatformService {

    private final AgentWorkflowGeneratorService agentWorkflowGeneratorService;
    private final McpToolFilteringService mcpToolFilteringService;
    private final AiAgentService aiAgentService;

    public AiAgentPlatformService(AgentWorkflowGeneratorService agentWorkflowGeneratorService, AiAgentService aiAgentService,
                         McpToolFilteringService mcpToolFilteringService) {
        this.agentWorkflowGeneratorService = agentWorkflowGeneratorService;
        this.mcpToolFilteringService = mcpToolFilteringService;
        this.aiAgentService = aiAgentService;
    }

    public Map<String, Object> createAgent(String task) throws ToolNotFoundException {
        Workflow workflow = agentWorkflowGeneratorService.generateAgentWorkflow(task);
        List<McpToolSpec> mcpToolSpecs = mcpToolFilteringService.getFilteredCallBackTools(workflow);
        AiAgentConfig aiAgentConfig = new AiAgentConfig(createAgentMetadata(workflow.aiAgentDescription()), mcpToolSpecs);

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
        return this.mcpToolFilteringService.getFilteredCallBackTools(
                this.agentWorkflowGeneratorService.generateAgentWorkflow(task));
    }

    private AiAgentMetadata createAgentMetadata(String description){
        return new AiAgentMetadata(java.util.UUID.randomUUID(), description);
    }
}