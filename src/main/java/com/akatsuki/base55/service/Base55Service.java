package com.akatsuki.base55.service;

import com.akatsuki.base55.domain.McpToolSpec;
import com.akatsuki.base55.domain.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class Base55Service {

    private final AgentWorkflowGeneratorService AgentWorkflowGeneratorService;
    private final McpToolFilteringService mcpToolFilteringService;

    public Base55Service(AgentWorkflowGeneratorService AgentWorkflowGeneratorService,
                         McpToolFilteringService mcpToolFilteringService) {
        this.AgentWorkflowGeneratorService = AgentWorkflowGeneratorService;
        this.mcpToolFilteringService = mcpToolFilteringService;
    }

    public Workflow generateAgentTasks(String task) {
        return AgentWorkflowGeneratorService.generateAgentTasks(task);
    }

    public ToolCallbackProvider getFilteredTools(String task) {
        return this.mcpToolFilteringService.getFilteredCallBackTools(
                this.AgentWorkflowGeneratorService.generateAgentTasks(task)
        );
    }
}
