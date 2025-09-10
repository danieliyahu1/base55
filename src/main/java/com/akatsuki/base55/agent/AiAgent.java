package com.akatsuki.base55.agent;

import com.akatsuki.base55.domain.SubTask;

import java.util.UUID;

public class AiAgent {
    private final UUID agentId;
    private final TaskDecomposer taskDecomposer;
    private final SubTaskExecutor subTaskExecutor;
    private final ResultEvaluator resultEvaluator;

    public AiAgent(TaskDecomposer taskDecomposer, SubTaskExecutor subTaskExecutor, ResultEvaluator resultEvaluator) {
        this.agentId = UUID.randomUUID();
        this.taskDecomposer = taskDecomposer;
        this.subTaskExecutor = subTaskExecutor;
        this.resultEvaluator = resultEvaluator;
    }

    public SubTask decomposeTask(String task, String currentState) {
        return taskDecomposer.decomposeTask(task, currentState);
    }
}
