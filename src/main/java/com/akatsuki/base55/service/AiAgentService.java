package com.akatsuki.base55.service;

import com.akatsuki.base55.agent.AgentFactory;
import com.akatsuki.base55.agent.AiAgent;
import com.akatsuki.base55.domain.SubTask;
import com.akatsuki.base55.domain.agent.AiAgentConfig;
import com.akatsuki.base55.domain.agent.AiAgentMetadata;
import com.akatsuki.base55.domain.mcp.tools.McpToolSpec;
import com.akatsuki.base55.entity.AiAgentConfigEntity;
import com.akatsuki.base55.entity.AiAgentMetadataEntity;
import com.akatsuki.base55.repository.AiAgentConfigRepository;
import com.akatsuki.base55.repository.AiAgentMetadataRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.akatsuki.base55.constant.AgentConstants.FIRST_DECOMPOSITION;

@Service
public class AiAgentService {

    private final AgentFactory agentFactory;
    private final Map<UUID, AiAgent> agents;
    private final AiAgentConfigRepository aiAgentRepository;
    private final AiAgentMetadataRepository aiAgentMetadataRepository;
    private final AgentToolService agentToolService;

    public AiAgentService(AiAgentConfigRepository aiAgentConfigRepository, AiAgentMetadataRepository aiAgentMetadataRepository,
                          AgentFactory agentFactory, AgentToolService agentToolService) {
        agents = new HashMap<>();
        this.aiAgentRepository = aiAgentConfigRepository;
        this.aiAgentMetadataRepository = aiAgentMetadataRepository;
        this.agentFactory = agentFactory;
        this.agentToolService = agentToolService;
    }

    public AiAgent createAgent(AiAgentConfig aiAgentConfig) {
        return registerAgent(
                agentFactory.createAgent(aiAgentConfig)
                , aiAgentConfig
        );
    }

    public SubTask decomposeTask(UUID agentId, String task){
        AiAgent agent = agents.get(agentId);
        if(agent == null){
            throw new IllegalArgumentException("Agent not found"); //needs to create custom exception
        }
        return agent.decomposeTask(task, FIRST_DECOMPOSITION);
    }

    private AiAgent registerAgent(AiAgent agent, AiAgentConfig aiAgentConfig){
        agents.put(agent.getAgentId(), agent);
        AiAgentMetadataEntity metadataEntity = saveAgentMetadataEntity(aiAgentConfig.metadata());
        List<UUID> toolIds = aiAgentConfig.mcpToolSpecs().stream().map(McpToolSpec::id).toList();
        aiAgentRepository.save(saveAgentConfigEntity(toolIds, metadataEntity));
        return agent;
    }

    private AiAgentMetadataEntity saveAgentMetadataEntity(AiAgentMetadata aiAgentMetadata){
        return AiAgentMetadataEntity.builder()
                .agentId(aiAgentMetadata.id())
                .description(aiAgentMetadata.description())
                .build();
    }

    private AiAgentConfigEntity saveAgentConfigEntity(List<UUID> toolIds, AiAgentMetadataEntity metadataEntity){
        return AiAgentConfigEntity.builder()
                .metadata(metadataEntity)
                .mcpToolSpecIds(toolIds)
                .build();
    }
}
