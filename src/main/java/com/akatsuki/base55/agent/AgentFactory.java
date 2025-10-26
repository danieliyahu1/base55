package com.akatsuki.base55.agent;

import com.akatsuki.base55.domain.agent.AiAgentConfig;
import com.akatsuki.base55.domain.mcp.tools.McpToolSpec;
import com.akatsuki.base55.service.ToolService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AgentFactory {
    private final ResultEvaluator resultEvaluator;
    private final TaskExecutorFactory taskExecutorFactory;
    private final ToolService agentToolService;

    public AgentFactory(ResultEvaluator resultEvaluator, TaskExecutorFactory taskExecutorFactory
                        , ToolService agentToolService) {
        this.resultEvaluator = resultEvaluator;
        this.taskExecutorFactory = taskExecutorFactory;
        this.agentToolService = agentToolService;
    }

    public AiAgent createAgent(AiAgentConfig aiAgentConfig) {
        return new AiAgent(aiAgentConfig.metadata(), taskExecutorFactory.create(getMatchingToolCallBacks(aiAgentConfig.mcpToolSpecs()), aiAgentConfig.metadata().agentSystemPrompt()), resultEvaluator);
    }

    private ToolCallbackProvider getMatchingToolCallBacks(List<McpToolSpec> mcpToolSpecs){
        return agentToolService.getToolCallbackProviderFromMcpToolSpecs(mcpToolSpecs);
    }
}
