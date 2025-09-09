package com.akatsuki.base55.agent;

import org.springframework.ai.tool.ToolCallbackProvider;

import java.util.UUID;

public class AiAgent {
    private final UUID agentId;
    private final TaskDecomposer taskDecomposer;
    private final SubTaskExecutor subTaskExecutor;
    private final ResultEvaluator resultEvaluator;

    public AiAgent(TaskDecomposer taskDecomposer, ToolCallbackProvider toolCallbackProvider, ResultEvaluator resultEvaluator) {
        this.agentId = UUID.randomUUID();
        this.taskDecomposer = taskDecomposer;
        this.subTaskExecutor = new SubTaskExecutor(toolCallbackProvider);
        this.resultEvaluator = resultEvaluator;
    }
}
