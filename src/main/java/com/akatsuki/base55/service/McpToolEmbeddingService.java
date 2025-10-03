package com.akatsuki.base55.service;

import com.akatsuki.base55.domain.mcp.tools.McpToolSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class McpToolEmbeddingService {

    private final VectorStore vectorStore;

    public McpToolEmbeddingService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void initializeMcpTools(List<McpToolSpec> tools) {
        log.info("initializing MCP tool embeddings in vector store.");
        saveMcpToolEmbeddingsInVectorStore(tools);
    }

    public List<Document> getSimilarToolByQueryAndTopK(String query, int topK) {
        log.info("fetching similar tool for query: %s".formatted(query));
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .build();
        List<Document> results = vectorStore.similaritySearch(searchRequest);
        if (results.isEmpty()) {
            log.warn("no similar tool found for query: %s".formatted(query));
            return null;
        }
        log.info("found similar tools for query: %s".formatted(query));
        return results;
    }

    public Document getToolById(String toolId) {
        log.info("fetching tool by id: %s".formatted(toolId));
        SearchRequest searchRequest = searchRequestById(toolId);
        List<Document> results = vectorStore.similaritySearch(searchRequest);
        if (results.isEmpty()) {
            log.warn("no tool found with id: %s".formatted(toolId));
            return null;
        }
        log.info("found tool with id: %s".formatted(toolId));
        return results.get(0);
    }

    private void saveMcpToolEmbeddingsInVectorStore(List<McpToolSpec> tools){
        log.info("attempting to save tool embeddings in vector store.");
        for(McpToolSpec toolSpec : tools){
            // Check if embedding already exists in vector store
            if(!documentExistById(toolSpec.id().toString()))
            {
                log.info("No embedding found for tool: %s, creating new one.".formatted(toolSpec.name()));
                vectorStore.add(List.of(createDocument(toolSpec)));
            }
            else {
                log.info("embedding already exists for tool: %s, skipping.".formatted(toolSpec.name()));
            }
        }
        log.info("successfully saved tool embeddings in vector store.");
    }

    private boolean documentExistById(String toolId) {
        SearchRequest searchRequest = searchRequestById(toolId);
        log.info("checking if document exists with id: %s".formatted(toolId));
        return !vectorStore.similaritySearch(searchRequest).isEmpty();
    }

    private SearchRequest searchRequestById(String toolId) {
        log.info("creating search request for document with id: %s".formatted(toolId));
        return SearchRequest.builder()
                .query("") // empty query, since we just filter by metadata
                .topK(1)
                .filterExpression("id == '" + toolId + "'")
                .build();
    }

    private Document createDocument(McpToolSpec toolSpec) {
        log.info("creating document for tool: %s".formatted(toolSpec.name()));
        return Document.builder()
                .text(toolSpec.description())
                .metadata("name", toolSpec.name())
                .metadata("serverName", toolSpec.serverName())
                .metadata("id", toolSpec.id().toString())
                .build();
    }
}
