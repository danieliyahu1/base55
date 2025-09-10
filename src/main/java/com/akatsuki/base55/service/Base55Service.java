package com.akatsuki.base55.service;

import com.akatsuki.base55.agent.AgentFactory;
import com.akatsuki.base55.domain.SubTask;
import com.akatsuki.base55.domain.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Base55Service {

    private final AgentWorkflowGeneratorService AgentWorkflowGeneratorService;
    private final McpToolFilteringService mcpToolFilteringService;
    private final AgentFactory agentFactory;

    public Base55Service(AgentWorkflowGeneratorService AgentWorkflowGeneratorService,
                         McpToolFilteringService mcpToolFilteringService, AgentFactory agentFactory) {
        this.AgentWorkflowGeneratorService = AgentWorkflowGeneratorService;
        this.mcpToolFilteringService = mcpToolFilteringService;
        this.agentFactory = agentFactory;
    }

    public Workflow generateAgentTasks(String task) {
        return AgentWorkflowGeneratorService.generateAgentTasks(task);
    }

    public ToolCallbackProvider getFilteredTools(String task) {
        return this.mcpToolFilteringService.getFilteredCallBackTools(
                this.AgentWorkflowGeneratorService.generateAgentTasks(task)
        );
    }

    public SubTask decomposeTask(String task) {
        return agentFactory.createAgent(getFilteredTools(task)).decomposeTask(task, "Initial State");
    }
}
