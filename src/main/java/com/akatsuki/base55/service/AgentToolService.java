package com.akatsuki.base55.service;

import com.akatsuki.base55.domain.mcp.tools.McpToolSpec;
import com.akatsuki.base55.entity.McpToolSpecEntity;
import com.akatsuki.base55.repository.McpToolSpecRepository;
import io.modelcontextprotocol.client.McpSyncClient;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AgentToolService {
    private final McpToolSpecRepository mcpToolSpecRepository;
    private final EmbeddingModel embeddingModel;
    ToolCallbackProvider toolCallbackProvider;
    List<McpSyncClient> mcpSyncClients;

    public AgentToolService(McpToolSpecRepository mcpToolSpecRepository, EmbeddingModel embeddingModel,
                            ToolCallbackProvider toolCallbackProvider,
                            List<McpSyncClient> mcpSyncClients) {
        this.mcpToolSpecRepository = mcpToolSpecRepository;
        this.embeddingModel = embeddingModel;
        this.toolCallbackProvider = toolCallbackProvider;
        this.mcpSyncClients = mcpSyncClients;
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

    @PostConstruct
    private void initializeMcpTools() {
        List<McpToolSpec> tools = mcpToolSpecs(mcpSyncClients);
        saveMcpTools(tools);
    }

    private List<McpToolSpec> mcpToolSpecs (List<McpSyncClient> mcpSyncClients) {
        return mcpSyncClients.stream().map(client -> client.listTools().tools().stream().map(tool -> new McpToolSpec(
                tool.name(),
                tool.description(),
                client.getServerInfo().name()
        )).toList()).flatMap(List::stream).toList();
    }

    private List<McpToolSpecEntity> saveMcpTools(List<McpToolSpec> tools) {
        return tools.stream()
                .map(tool -> mcpToolSpecRepository
                        .findByServerNameAndName(tool.serverName(), tool.name())
                        .orElseGet(() -> mcpToolSpecRepository.save(
                                new McpToolSpecEntity(tool.id(), tool.serverName(), tool.name(), tool.description(), getEmbedding(tool.description()))
                        ))
                ).toList();
    }

    private float[] getEmbedding(String description) {
        return embeddingModel.embed(description);
    }

    private String normalizeToolName(String toolName) {
        return toolName.replace("-", "_");
    }
}
