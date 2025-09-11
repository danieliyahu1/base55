package com.akatsuki.base55.agent;

import com.akatsuki.base55.domain.AiAgentConfig;
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
        return new AiAgent(aiAgentConfig.agentDescription(), taskDecomposer, subTaskExecutorFactory.create(aiAgentConfig.toolCallbackProvider()), resultEvaluator);
    }
}
