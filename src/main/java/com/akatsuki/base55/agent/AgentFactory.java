package com.akatsuki.base55.agent;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;

@Component
public class AgentFactory {
    private final TaskDecomposer taskDecomposer;
    private final ResultEvaluator resultEvaluator;
    private final SubTaskExecutorFactory subTaskExecutorFactory;

    public AgentFactory(TaskDecomposer taskDecomposer, ResultEvaluator resultEvaluator, SubTaskExecutorFactory subTaskExecutorFactory) {
        this.taskDecomposer = taskDecomposer;
        this.resultEvaluator = resultEvaluator;
        this.subTaskExecutorFactory = subTaskExecutorFactory;
    }

    public AiAgent createAgent(ToolCallbackProvider toolCallbackProvider) {
        return new AiAgent(taskDecomposer, subTaskExecutorFactory.create(toolCallbackProvider), resultEvaluator);
    }
}
