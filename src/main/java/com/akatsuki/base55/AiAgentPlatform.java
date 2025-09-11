package com.akatsuki.base55;

import com.akatsuki.base55.agent.AgentFactory;
import com.akatsuki.base55.agent.AiAgent;
import com.akatsuki.base55.domain.AiAgentConfig;
import com.akatsuki.base55.domain.workflow.Workflow;
import com.akatsuki.base55.service.AgentWorkflowGeneratorService;
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

    public AiAgentPlatform(AgentWorkflowGeneratorService agentWorkflowGeneratorService,
                         McpToolFilteringService mcpToolFilteringService, AgentFactory agentFactory) {
        this.agentWorkflowGeneratorService = agentWorkflowGeneratorService;
        this.mcpToolFilteringService = mcpToolFilteringService;
        this.agentFactory = agentFactory;
    }

    public Map<String, Object> createAgent(String task) {
        Workflow workflow = agentWorkflowGeneratorService.generateAgentWorkflow(task);
        ToolCallbackProvider toolCallbackProvider = mcpToolFilteringService.getFilteredCallBackTools(workflow);
        AiAgent agent =  agentFactory.createAgent(
                new AiAgentConfig(workflow.aiAgentDescription(), toolCallbackProvider)
        );
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("agent", agent);
        responseMap.put("Tools", toolCallbackProvider);
        return responseMap;

    }

    public Workflow generateAgentWorkflow(String task) {
        return agentWorkflowGeneratorService.generateAgentWorkflow(task);
    }

    public ToolCallbackProvider getFilteredTools(String task) {
        return this.mcpToolFilteringService.getFilteredCallBackTools(
                this.agentWorkflowGeneratorService.generateAgentWorkflow(task));
    }
}