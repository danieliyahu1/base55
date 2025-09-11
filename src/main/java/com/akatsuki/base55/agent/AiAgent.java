package com.akatsuki.base55.agent;

import com.akatsuki.base55.domain.SubTask;
import lombok.Getter;

import java.util.UUID;

import static com.akatsuki.base55.constant.AgentConstants.FIRST_DECOMPOSITION;

public class AiAgent {

    @Getter
    private final UUID agentId;
    @Getter
    private final String agentDescription;
    private final TaskDecomposer taskDecomposer;
    private final SubTaskExecutor subTaskExecutor;
    private final ResultEvaluator resultEvaluator;

    public AiAgent(String agentDescription, TaskDecomposer taskDecomposer, SubTaskExecutor subTaskExecutor, ResultEvaluator resultEvaluator) {
        this.agentId = UUID.randomUUID();
        this.agentDescription = agentDescription;
        this.taskDecomposer = taskDecomposer;
        this.subTaskExecutor = subTaskExecutor;
        this.resultEvaluator = resultEvaluator;
    }

    public SubTask decomposeTask(String task, String currentState) {
        return taskDecomposer.decomposeTask(task, currentState);
    }
}
