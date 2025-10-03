package com.akatsuki.base55.agent;

import com.akatsuki.base55.domain.AiResponseDomain;
import com.akatsuki.base55.domain.SubTask;
import com.akatsuki.base55.domain.agent.AiAgentMetadata;
import com.akatsuki.base55.dto.AiResponseDTO;
import com.akatsuki.base55.enums.AgentFlowControl;
import lombok.Getter;

import java.util.UUID;

public class AiAgent {

    @Getter
    private final UUID agentId;
    private final String systemPrompt;
    @Getter
    private final String agentDescription;
    private final TaskDecomposer taskDecomposer;
    private final SubTaskExecutor subTaskExecutor;
    private final ResultEvaluator resultEvaluator;

    public AiAgent(AiAgentMetadata aiAgentMetadata, TaskDecomposer taskDecomposer, SubTaskExecutor subTaskExecutor, ResultEvaluator resultEvaluator) {
        this.agentId = aiAgentMetadata.id();
        this.systemPrompt = aiAgentMetadata.systemPrompt();
        this.agentDescription = aiAgentMetadata.description();
        this.taskDecomposer = taskDecomposer;
        this.subTaskExecutor = subTaskExecutor;
        this.resultEvaluator = resultEvaluator;
    }

//    public SubTask decomposeTask(String task) {
//        return taskDecomposer.getNextSubTask(task,
//                generateSystemMessageForDecompposeTask(task)
//                , agentId.toString());
//    }

    public AiResponseDomain executeTask(String task) {
        SubTask firstSubTask = taskDecomposer.firstDecomposition(
                generateSystemMessageForDecompposeTask(task)
                , agentId.toString());


    }

    private AiResponseDomain executeSubTask(SubTask subTask, String originalTask) {
        return subTaskExecutor.executeSubTask(
                subTask,
                agentId.toString()
        );
    }




    private String generateSystemMessageForDecompposeTask(String task) {
        return this.systemPrompt
                + "\n"
                + "The original task from the user: \n"
                + task;
    }


}
