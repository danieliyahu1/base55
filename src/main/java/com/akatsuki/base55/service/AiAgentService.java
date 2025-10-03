package com.akatsuki.base55.service;

import com.akatsuki.base55.agent.AgentFactory;
import com.akatsuki.base55.agent.AiAgent;
import com.akatsuki.base55.domain.AiResponseDomain;
import com.akatsuki.base55.domain.SubTask;
import com.akatsuki.base55.domain.agent.AiAgentConfig;
import com.akatsuki.base55.domain.agent.AiAgentMetadata;
import com.akatsuki.base55.domain.mcp.tools.McpToolSpec;
import com.akatsuki.base55.dto.AiResponseDTO;
import com.akatsuki.base55.entity.AiAgentConfigEntity;
import com.akatsuki.base55.entity.AiAgentMetadataEntity;
import com.akatsuki.base55.exception.AgentNotFound;
import com.akatsuki.base55.repository.AiAgentConfigRepository;
import com.akatsuki.base55.repository.AiAgentMetadataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.akatsuki.base55.constant.AgentConstants.FIRST_DECOMPOSITION;
import static com.akatsuki.base55.exception.constant.ExceptionConstant.AGENT_NOT_FOUND_EXCEPTION_MESSAGE;

@Service
@Slf4j
public class AiAgentService {

    private final AgentFactory agentFactory;
    private final Map<UUID, AiAgent> agents;
    private final AiAgentConfigRepository aiAgentConfigRepository;
    private final AiAgentMetadataRepository aiAgentMetadataRepository;
    private final ToolService toolService;

    public AiAgentService(AiAgentConfigRepository aiAgentConfigRepository, AiAgentMetadataRepository aiAgentMetadataRepository,
                          AgentFactory agentFactory, ToolService toolService) {
        agents = new HashMap<>();
        this.aiAgentConfigRepository = aiAgentConfigRepository;
        this.aiAgentMetadataRepository = aiAgentMetadataRepository;
        this.agentFactory = agentFactory;
        this.toolService = toolService;
    }

    @Transactional
    public AiAgent createAgent(AiAgentConfig aiAgentConfig) {
        saveAgentConfig(aiAgentConfig);
        return registerAgent(
                agentFactory.createAgent(aiAgentConfig)
        );
    }

    public SubTask decomposeTask(UUID agentId, String task){
        AiAgent agent = agents.get(agentId);
        if(agent == null){
            throw new IllegalArgumentException("Agent not found"); //needs to create custom exception
        }
        return null;
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void initializeAgents(){
        log.info("Initializing AI Agents from DB configurations...");
        List<AiAgentConfig> agentConfigs = fetchAgentConfigs();
        agentConfigs.forEach(aiAgentConfig ->
                registerAgent(agentFactory.createAgent(aiAgentConfig))
        );
        log.info("Initialized {} AI Agents", agents.size());
    }

    private List<AiAgentConfig> fetchAgentConfigs(){
        log.info("Fetching AI Agent configurations from DB...");
        List<AiAgentConfigEntity> agentConfigEntities = fetchAiConfigsFromDb();
        Map<UUID, List<McpToolSpec>> tools = getToolsByToolIds(agentConfigEntities);
        return convertEntitiesToDomain(agentConfigEntities, tools);
    }

    private List<AiAgentConfig> convertEntitiesToDomain(List<AiAgentConfigEntity> entities, Map<UUID, List<McpToolSpec>> toolsByAgentId){
        return entities.stream().map(
                entity -> new AiAgentConfig(
                        new AiAgentMetadata(
                                entity.getMetadata().getAgentId(),
                                entity.getMetadata().getDescription(),
                                entity.getMetadata().getSystemPrompt()
                        ),
                        toolsByAgentId.get(entity.getMetadata().getAgentId())
                )
        ).toList();
    }

    private Map<UUID, List<McpToolSpec>> getToolsByToolIds(List<AiAgentConfigEntity> agentConfigEntities){
        Map<UUID, List<McpToolSpec>> agentTools = new HashMap<>();
        for(AiAgentConfigEntity entity : agentConfigEntities){
            List<McpToolSpec> tools = toolService.getToolsByToolIds(entity.getMcpToolSpecIds());
            agentTools.put(entity.getMetadata().getAgentId(), tools);
        }
        return agentTools;
    }

    private List<AiAgentConfigEntity> fetchAiConfigsFromDb(){
        return aiAgentConfigRepository.findAll();
    }

    private AiAgentConfigEntity saveAgentConfig( AiAgentConfig aiAgentConfig){
        AiAgentMetadataEntity metadataEntity = saveAgentMetadataEntity(aiAgentConfig.metadata());
        List<UUID> toolIds = aiAgentConfig.mcpToolSpecs().stream().map(McpToolSpec::id).toList();
        return saveAgentConfig(toolIds, metadataEntity);
    }

    private AiAgent registerAgent(AiAgent agent){
        log.info("Registering AI Agent with ID: {}", agent.getAgentId());
        agents.put(agent.getAgentId(), agent);
        return agent;
    }

    private AiAgentMetadataEntity saveAgentMetadataEntity(AiAgentMetadata aiAgentMetadata){
        return aiAgentMetadataRepository.save(
                AiAgentMetadataEntity.builder()
                        .agentId(aiAgentMetadata.id())
                        .description(aiAgentMetadata.description())
                        .systemPrompt(aiAgentMetadata.systemPrompt())
                        .build()
        );
    }

    private AiAgentConfigEntity saveAgentConfig(List<UUID> toolIds, AiAgentMetadataEntity metadataEntity){
        return aiAgentConfigRepository.save(AiAgentConfigEntity.builder()
                .metadata(metadataEntity)
                .mcpToolSpecIds(toolIds)
                .build()
        );
    }

    public AiResponseDomain chatWithAgent(String id, String prompt) throws AgentNotFound {
        UUID agentId = UUID.fromString(id);
        AiAgent agent = agents.get(agentId);
        if(agent == null){
            throw new AgentNotFound(String.format(String.format(AGENT_NOT_FOUND_EXCEPTION_MESSAGE, id))); //needs to create custom exception
        }
        return null;
    }
}
