package com.akatsuki.base55;

import com.akatsuki.base55.agent.AgentFactory;
import com.akatsuki.base55.agent.AiAgent;
import com.akatsuki.base55.domain.agent.AiAgentConfig;
import com.akatsuki.base55.domain.agent.AiAgentMetadata;
import com.akatsuki.base55.domain.workflow.Workflow;
import com.akatsuki.base55.exception.ToolNotFoundException;
import com.akatsuki.base55.service.AgentWorkflowGeneratorService;
import com.akatsuki.base55.service.AiAgentService;
import com.akatsuki.base55.service.McpToolFilteringService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AiAgentPlatform {

    private final AgentWorkflowGeneratorService agentWorkflowGeneratorService;
    private final McpToolFilteringService mcpToolFilteringService;
    private final AgentFactory agentFactory;
    private final AiAgentService aiAgentService;

    public AiAgentPlatform(AgentWorkflowGeneratorService agentWorkflowGeneratorService, AiAgentService aiAgentService,
                         McpToolFilteringService mcpToolFilteringService, AgentFactory agentFactory) {
        this.agentWorkflowGeneratorService = agentWorkflowGeneratorService;
        this.mcpToolFilteringService = mcpToolFilteringService;
        this.agentFactory = agentFactory;
        this.aiAgentService = aiAgentService;
    }

    public Map<String, Object> createAgent(String task) throws ToolNotFoundException {
        Workflow workflow = agentWorkflowGeneratorService.generateAgentWorkflow(task);
        ToolCallbackProvider toolCallbackProvider = mcpToolFilteringService.getFilteredCallBackTools(workflow);
        AiAgentConfig aiAgentConfig = new AiAgentConfig(createAgentMetadata(workflow.aiAgentDescription()), toolCallbackProvider.getToolCallbacks());

        AiAgent agent =  agentFactory.createAgent(
                aiAgentConfig
        );
        aiAgentService.registerAgent(agent, aiAgentConfig);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("agent", agent);
        responseMap.put("Tools", toolCallbackProvider);
        return responseMap;
    }

    public Workflow generateAgentWorkflow(String task) {
        return agentWorkflowGeneratorService.generateAgentWorkflow(task);
    }

    public ToolCallbackProvider getFilteredTools(String task) throws ToolNotFoundException {
        return this.mcpToolFilteringService.getFilteredCallBackTools(
                this.agentWorkflowGeneratorService.generateAgentWorkflow(task));
    }

    private AiAgentMetadata createAgentMetadata(String description){
        return new AiAgentMetadata(java.util.UUID.randomUUID(), description);
    }
}