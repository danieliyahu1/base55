package com.akatsuki.base55.service;

import com.akatsuki.base55.domain.mcp.tools.McpToolSpec;
import com.akatsuki.base55.entity.McpToolSpecEntity;
import com.akatsuki.base55.repository.McpToolSpecRepository;
import io.modelcontextprotocol.client.McpSyncClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class McpToolSpecService {

    private final McpToolSpecRepository mcpToolSpecRepository;
    private final ToolCallbackProvider toolCallbackProvider;
    private final List<McpSyncClient> mcpSyncClients;

    public McpToolSpecService(McpToolSpecRepository mcpToolSpecRepository, ToolCallbackProvider toolCallbackProvider
                                , List<McpSyncClient> mcpSyncClients) {
        this.mcpToolSpecRepository = mcpToolSpecRepository;
        this.toolCallbackProvider = toolCallbackProvider;
        this.mcpSyncClients = mcpSyncClients;
    }

    public List<McpToolSpec> initializeMcpTools() {
        log.info("Initializing MCP tools from MCP clients.");
        return saveMcpTools(getMcpToolSpecsFromMcpClients(mcpSyncClients)).stream().map(
                this::mcpToolSpecEntityToMcpToolSpec
        ).toList();
    }

    public List<McpToolSpec> getAllMcpToolSpecs() {
        return mcpToolSpecRepository.findAll().stream()
                .map(this::mcpToolSpecEntityToMcpToolSpec)
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

    public List<McpToolSpec> getMcpToolSpecsFromMcpClients (List<McpSyncClient> mcpSyncClients) {
        return mcpSyncClients.stream().map(client -> client.listTools().tools().stream().map(tool -> McpToolSpec.newSpec(
                tool.name(),
                tool.description(),
                client.getServerInfo().name()
        )).toList()).flatMap(List::stream).toList();
    }

    public McpToolSpec getToolByToolId(UUID toolId) {
        return mcpToolSpecRepository.findByToolId(toolId)
                .map(this::mcpToolSpecEntityToMcpToolSpec)
                .orElse(null);
    }

    public List<McpToolSpec> getToolsByToolIds(List<UUID> ids) {
        return mcpToolSpecRepository.findAllByToolIdIn(ids).stream()
                .map(this::mcpToolSpecEntityToMcpToolSpec)
                .toList();
    }

    private McpToolSpec mcpToolSpecEntityToMcpToolSpec(McpToolSpecEntity entity) {
        return McpToolSpec.fromDb(entity.getToolId(), entity.getName(), entity.getDescription(), entity.getServerName());
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
        String description = (tool.description() != null) ? tool.description() : tool.name();
        return mcpToolSpecRepository.save(
                new McpToolSpecEntity(tool.id(), tool.serverName(), tool.name(), description)
        );
    }

    private String normalizeToolName(String toolName) {
        return toolName.replace("-", "_");
    }
}
