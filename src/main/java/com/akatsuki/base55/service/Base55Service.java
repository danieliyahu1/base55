package com.akatsuki.base55.service;

import com.akatsuki.base55.AiAgentPlatform;
import com.akatsuki.base55.agent.AiAgent;
import com.akatsuki.base55.domain.SubTask;
import com.akatsuki.base55.domain.mcp.tools.McpToolSpec;
import com.akatsuki.base55.domain.workflow.Workflow;
import com.akatsuki.base55.exception.Base55Exception;
import com.akatsuki.base55.exception.ToolNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class Base55Service {

    private final AiAgentPlatform aiAgentPlatform;
    private final AiAgentService aiAgentService;

    public Base55Service(AiAgentPlatform aiAgentPlatform, AiAgentService aiAgentService) {
        this.aiAgentPlatform = aiAgentPlatform;
        this.aiAgentService = aiAgentService;
    }

    public Workflow generateAgentWorkflow(String task) {
        return aiAgentPlatform.generateAgentWorkflow(task);
    }

    public List<McpToolSpec> getFilteredTools(String task) throws ToolNotFoundException {
        return aiAgentPlatform.getFilteredTools(task);
    }

    public Map<String, Object> createAgent(String task) throws Base55Exception {
        try{
            return aiAgentPlatform.createAgent(task);
        } catch (ToolNotFoundException e) {
            log.error("Error creating agent: {}", e.getMessage());
            throw new Base55Exception(e.getMessage());
        }
    }
}