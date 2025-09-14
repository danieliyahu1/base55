package com.akatsuki.base55.agent;

import com.akatsuki.base55.domain.agent.AiAgentConfig;
import com.akatsuki.base55.domain.mcp.tools.McpToolSpec;
import com.akatsuki.base55.registry.ToolRegistry;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AgentFactory {
    private final TaskDecomposer taskDecomposer;
    private final ResultEvaluator resultEvaluator;
    private final SubTaskExecutorFactory subTaskExecutorFactory;
    private final ToolRegistry toolRegistry;

    public AgentFactory(TaskDecomposer taskDecomposer, ResultEvaluator resultEvaluator, SubTaskExecutorFactory subTaskExecutorFactory
                        , ToolRegistry toolRegistry) {
        this.taskDecomposer = taskDecomposer;
        this.resultEvaluator = resultEvaluator;
        this.subTaskExecutorFactory = subTaskExecutorFactory;
        this.toolRegistry = toolRegistry;
    }

    public AiAgent createAgent(AiAgentConfig aiAgentConfig) {
        return new AiAgent(aiAgentConfig.metadata(), taskDecomposer, subTaskExecutorFactory.create(getMatchingToolCallBacks(aiAgentConfig.mcpToolSpecs())), resultEvaluator);
    }

    private List<ToolCallback> getMatchingToolCallBacks(List<McpToolSpec> mcpToolSpecs){
        return toolRegistry.getMatchingToolCallBacks(mcpToolSpecs);
    }
}
