package com.akatsuki.base55.service;

import com.akatsuki.base55.AiAgentPlatform;
import com.akatsuki.base55.agent.AiAgent;
import com.akatsuki.base55.domain.SubTask;
import com.akatsuki.base55.domain.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

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

    public ToolCallbackProvider getFilteredTools(String task) {
        return aiAgentPlatform.getFilteredTools(task);
    }

    public Map<String, Object> createAgent(String task) {
        return aiAgentPlatform.createAgent(task);
    }
}