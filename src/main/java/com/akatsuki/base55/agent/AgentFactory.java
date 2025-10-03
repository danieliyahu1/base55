package com.akatsuki.base55.agent;

import com.akatsuki.base55.domain.agent.AiAgentConfig;
import com.akatsuki.base55.domain.mcp.tools.McpToolSpec;
import com.akatsuki.base55.service.ToolService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AgentFactory {
    private final TaskDecomposer taskDecomposer;
    private final ResultEvaluator resultEvaluator;
    private final SubTaskExecutorFactory subTaskExecutorFactory;
    private final ToolService agentToolService;

    public AgentFactory(TaskDecomposer taskDecomposer, ResultEvaluator resultEvaluator, SubTaskExecutorFactory subTaskExecutorFactory
                        , ToolService agentToolService) {
        this.taskDecomposer = taskDecomposer;
        this.resultEvaluator = resultEvaluator;
        this.subTaskExecutorFactory = subTaskExecutorFactory;
        this.agentToolService = agentToolService;
    }

    public AiAgent createAgent(AiAgentConfig aiAgentConfig) {
        return new AiAgent(aiAgentConfig.metadata(), taskDecomposer, subTaskExecutorFactory.create(getMatchingToolCallBacks(aiAgentConfig.mcpToolSpecs())), resultEvaluator);
    }

    private ToolCallbackProvider getMatchingToolCallBacks(List<McpToolSpec> mcpToolSpecs){
        return agentToolService.getToolCallbackProviderFromMcpToolSpecs(mcpToolSpecs);
    }
}
