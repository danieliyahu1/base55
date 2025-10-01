package com.akatsuki.base55.service;

import com.akatsuki.base55.domain.mcp.tools.McpToolSpec;
import com.akatsuki.base55.entity.McpToolSpecEmbeddingEntity;
import com.akatsuki.base55.entity.McpToolSpecEntity;
import com.akatsuki.base55.exception.ToolNotFoundException;
import com.akatsuki.base55.repository.McpToolSpecEmbeddingRepository;
import com.akatsuki.base55.repository.McpToolSpecRepository;
import io.modelcontextprotocol.client.McpSyncClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.akatsuki.base55.exception.constant.ExceptionConstant.TOOL_NOT_FOUND_BY_NAME_AND_SERVER_EXCEPTION_MESSAGE;

@Slf4j
@Service
public class AgentToolService {
    private final McpToolSpecRepository mcpToolSpecRepository;
    private final McpToolSpecEmbeddingRepository mcpToolSpecEmbeddingRepository;
    private final EmbeddingModel embeddingModel;
    ToolCallbackProvider toolCallbackProvider;
    List<McpSyncClient> mcpSyncClients;

    public AgentToolService(McpToolSpecRepository mcpToolSpecRepository, EmbeddingModel embeddingModel,
                            ToolCallbackProvider toolCallbackProvider, McpToolSpecEmbeddingRepository mcpToolSpecEmbeddingRepository,
                            List<McpSyncClient> mcpSyncClients) {
        this.mcpToolSpecRepository = mcpToolSpecRepository;
        this.embeddingModel = embeddingModel;
        this.toolCallbackProvider = toolCallbackProvider;
        this.mcpSyncClients = mcpSyncClients;
        this.mcpToolSpecEmbeddingRepository = mcpToolSpecEmbeddingRepository;
    }

    public List<McpToolSpec> getAllMcpToolSpecs() {
        return mcpToolSpecRepository.findAll().stream()
                .map(entity -> new McpToolSpec(entity.getName(), entity.getDescription(), entity.getServerName()))
                .toList();
    }

    public ToolCallbackProvider getToolCallbackProviderFromMcpToolSpecs(List<McpToolSpec> mcpToolSpecs){
        return ToolCallbackProvider.from(
                Arrays.stream(toolCallbackProvider.getToolCallbacks())
                        .filter(
                                tool -> mcpToolSpecs.stream().anyMatch(
                                        spec -> tool.getToolDefinition().name().contains(normalizeToolName(spec.name()))
                                                && tool.getToolDefinition().description().equals(spec.description())
                                )
                        )
                        .toList()
        );
    }

    @EventListener(ApplicationReadyEvent.class)
    private void initializeMcpTools() throws ToolNotFoundException {
        List<McpToolSpec> tools = mcpClientToMcpToolSpec(mcpSyncClients);
        saveMcpTools(tools);
        saveMcpToolEmbeddings(tools);
    }

    private List<McpToolSpec> mcpClientToMcpToolSpec (List<McpSyncClient> mcpSyncClients) {
        return mcpSyncClients.stream().map(client -> client.listTools().tools().stream().map(tool -> new McpToolSpec(
                tool.name(),
                tool.description(),
                client.getServerInfo().name()
        )).toList()).flatMap(List::stream).toList();
    }

    private List<McpToolSpecEntity> saveMcpTools(List<McpToolSpec> tools) {
        return tools.stream()
                .map(tool -> {
                    // Log BEFORE the database query
                    log.info("Attempting to find tool: ServerName={}, ToolName={}", tool.serverName(), tool.name());

                    return mcpToolSpecRepository
                            .findByServerNameAndName(tool.serverName(), tool.name())
                            .map(entity -> {
                                // Log AFTER the database query (Found)
                                log.info("Tool found: ServerName={}, ToolName={}", tool.serverName(), tool.name());
                                return entity;
                            })
                            .orElseGet(() -> {
                                // Log AFTER the database query (Not Found/Using orElseGet)
                                log.warn("Tool NOT found: ServerName={}, ToolName={}. Creating new one.", tool.serverName(), tool.name());

                                // Call the extracted method for clarity
                                return saveMcpTool(tool);
                            });
                }).toList();
    }

    private McpToolSpecEntity saveMcpTool(McpToolSpec tool) {
        return mcpToolSpecRepository.save(
                new McpToolSpecEntity(tool.id(), tool.serverName(), tool.name(), tool.description())
        );
    }

    private List<McpToolSpecEmbeddingEntity> saveMcpToolEmbeddings(List<McpToolSpec> tools) throws ToolNotFoundException {
        List<McpToolSpecEmbeddingEntity> mcpToolSpecEmbeddings = new ArrayList<>();
        log.info("attempting to save tool embeddings.");
        for(McpToolSpec toolSpec : tools){
            McpToolSpecEntity mcpToolSpecEntity = mcpToolSpecRepository.findByServerNameAndName(toolSpec.serverName(), toolSpec.name())
                    .orElseThrow(() -> new ToolNotFoundException(TOOL_NOT_FOUND_BY_NAME_AND_SERVER_EXCEPTION_MESSAGE.formatted(toolSpec.name(), toolSpec.serverName())));
            if(!mcpToolSpecEmbeddingRepository.existsByToolSpecId(mcpToolSpecEntity.getToolId())){
                log.info("No embedding found for tool: %s, creating new one.".formatted(toolSpec.name()));
                McpToolSpecEmbeddingEntity mcpToolSpecEmbeddingEntity = saveMcpToolEmbedding(toolSpec, mcpToolSpecEntity);
                mcpToolSpecEmbeddings.add(mcpToolSpecEmbeddingEntity);
                log.info("saved tool embedding with value: %s".formatted(Arrays.toString(mcpToolSpecEmbeddingEntity.getEmbedding())));
            }
        }
        log.info("successfully saved tool embeddings.");
        return mcpToolSpecEmbeddings;
    }

    private McpToolSpecEmbeddingEntity saveMcpToolEmbedding(McpToolSpec tool, McpToolSpecEntity mcpToolSpecEntity) throws ToolNotFoundException {
        float[] embedding = getEmbedding(tool.description());
        McpToolSpecEmbeddingEntity embeddingEntity = McpToolSpecEmbeddingEntity.builder()
                .toolSpecId(mcpToolSpecEntity.getToolId())
                .embedding(embedding)
                .build();

        return mcpToolSpecEmbeddingRepository.save(embeddingEntity);
    }

    private float[] getEmbedding(String description) {
        return embeddingModel.embed(description);
    }

//    private Float[] getEmbedding(String description) {
//        float[] primitive = embeddingModel.embed(description); // your existing float[]
//
//        // convert to boxed Float[]
//        Float[] boxed = new Float[primitive.length];
//        for (int i = 0; i < primitive.length; i++) {
//            boxed[i] = primitive[i]; // auto-boxing
//        }
//
//        return boxed;
//    }

    private String normalizeToolName(String toolName) {
        return toolName.replace("-", "_");
    }
}
