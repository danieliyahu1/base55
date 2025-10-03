package com.akatsuki.base55.service;

import com.akatsuki.base55.domain.mcp.tools.McpToolSpec;
import com.akatsuki.base55.exception.ToolNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ToolService {
    private final McpToolSpecService mcpToolSpecService;
    private final McpToolEmbeddingService mcpToolEmbeddingService;

    public ToolService(McpToolEmbeddingService mcpToolEmbeddingService, McpToolSpecService mcpToolSpecService) {
        this.mcpToolEmbeddingService = mcpToolEmbeddingService;
        this.mcpToolSpecService = mcpToolSpecService;
    }

    public List<McpToolSpec> getAllMcpToolSpecs() {
        return mcpToolSpecService.getAllMcpToolSpecs();
    }

    public ToolCallbackProvider getToolCallbackProviderFromMcpToolSpecs(List<McpToolSpec> mcpToolSpecs){
        return mcpToolSpecService.getToolCallbackProviderFromMcpToolSpecs(mcpToolSpecs);
    }

    public List<Document> getSimilarDocumentsByQueryAndTopK(String query, int topK) {
        return mcpToolEmbeddingService.getSimilarToolByQueryAndTopK(query, topK);
    }

    public List<McpToolSpec> getSimilarToolsByQueryAndTopK(String query, int topK) {
        return mcpToolEmbeddingService.getSimilarToolByQueryAndTopK(query, topK).stream().map(
                doc -> mcpToolSpecService.getToolByToolId(UUID.fromString((String) doc.getMetadata().get("id"))
                )).toList();
    }

    public Document getToolById(String id) {
        return mcpToolEmbeddingService.getToolById(id);
    }

    @EventListener(ApplicationReadyEvent.class)
    private void initializeMcpTools() throws ToolNotFoundException {
        List<McpToolSpec> tools = mcpToolSpecService.initializeMcpTools();
        mcpToolEmbeddingService.initializeMcpTools(tools);
    }
}
