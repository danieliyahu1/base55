package com.akatsuki.base55.agent;

import com.akatsuki.base55.domain.agent.AiAgentConfig;
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

    public AiAgent createAgent(AiAgentConfig aiAgentConfig) {
        return new AiAgent(aiAgentConfig.metadata(), taskDecomposer, subTaskExecutorFactory.create(aiAgentConfig.toolCallbacks()), resultEvaluator);
    }
}
