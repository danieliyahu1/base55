package com.akatsuki.base55.agent;

import com.akatsuki.base55.domain.SubTask;
import com.akatsuki.base55.domain.agent.AiAgentMetadata;
import com.akatsuki.base55.dto.AiResponseDTO;
import lombok.Getter;

import java.util.UUID;

public class AiAgent {

    @Getter
    private final UUID agentId;
    @Getter
    private final String agentDescription;
    private final TaskDecomposer taskDecomposer;
    private final SubTaskExecutor subTaskExecutor;
    private final ResultEvaluator resultEvaluator;

    public AiAgent(AiAgentMetadata aiAgentMetadata, TaskDecomposer taskDecomposer, SubTaskExecutor subTaskExecutor, ResultEvaluator resultEvaluator) {
        this.agentId = aiAgentMetadata.id();
        this.agentDescription = aiAgentMetadata.description();
        this.taskDecomposer = taskDecomposer;
        this.subTaskExecutor = subTaskExecutor;
        this.resultEvaluator = resultEvaluator;
    }

    public SubTask decomposeTask(String task, String currentState) {
        return taskDecomposer.decomposeTask(task, currentState);
    }
}
