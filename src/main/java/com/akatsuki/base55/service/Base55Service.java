package com.akatsuki.base55.service;

import com.akatsuki.base55.domain.agent.AiAgentMetadata;
import com.akatsuki.base55.domain.agent.TaskExecutorResponse;
import com.akatsuki.base55.domain.mcp.tools.McpToolSpec;
import com.akatsuki.base55.domain.workflow.Workflow;
import com.akatsuki.base55.exception.AgentNotFound;
import com.akatsuki.base55.exception.Base55Exception;
import com.akatsuki.base55.exception.ToolNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class Base55Service {

    private final AiAgentPlatformService aiAgentPlatform;
    private final AiAgentService aiAgentService;

    public Base55Service(AiAgentPlatformService aiAgentPlatform, AiAgentService aiAgentService) {
        this.aiAgentPlatform = aiAgentPlatform;
        this.aiAgentService = aiAgentService;
    }

    public Workflow generateAgentWorkflow(String task) {
        return aiAgentPlatform.generateAgentWorkflow(task);
    }

    public List<McpToolSpec> getFilteredTools(String task) throws ToolNotFoundException, Base55Exception {
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

    public TaskExecutorResponse executeTask(String id, String prompt) throws AgentNotFound {
        return aiAgentService.executeTask(id, prompt);
    }

    public List<AiAgentMetadata> getAllAgents() {
        return aiAgentService.getAllAgents();
    }

    public List<McpToolSpec> getAgentTools(String agentId) {
        return aiAgentService.getAgentTools(agentId);
    }
}