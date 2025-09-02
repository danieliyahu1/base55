package com.akatsuki.base55.service;

import com.akatsuki.base55.domain.workflow.Workflow;
import com.akatsuki.base55.domain.workflow.step.WorkflowStep;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class Base55Service {

    private final AgentTasksGeneratorService agentTasksGeneratorService;
    private final McpToolFilteringService mcpToolFilteringService;

    public Base55Service(AgentTasksGeneratorService agentTasksGeneratorService,
                         McpToolFilteringService mcpToolFilteringService) {
        this.agentTasksGeneratorService = agentTasksGeneratorService;
        this.mcpToolFilteringService = mcpToolFilteringService;
    }

    public Workflow generateAgentTasks (String task) {
        return agentTasksGeneratorService.generateAgentTasks(task);
    }

    public List<McpSchema.Tool> getFilteredTools(String task){
        return this.mcpToolFilteringService.getFilteredTools(
                this.agentTasksGeneratorService.generateAgentTasks(task)
        );
    }
}
