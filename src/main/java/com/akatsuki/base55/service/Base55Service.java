package com.akatsuki.base55.service;

import com.akatsuki.base55.domain.McpToolSpec;
import com.akatsuki.base55.domain.workflow.Workflow;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public Workflow generateAgentTasks(String task) {
        return agentTasksGeneratorService.generateAgentTasks(task);
    }

    public Map<String, List<McpToolSpec>> getFilteredTools(String task) {
        return this.mcpToolFilteringService.getFilteredTools(
                this.agentTasksGeneratorService.generateAgentTasks(task)
        );
    }
}
