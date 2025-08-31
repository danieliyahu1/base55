package com.akatsuki.base55.service;

import com.akatsuki.base55.domain.workflow.Workflow;
import com.akatsuki.base55.domain.workflow.step.WorkflowStep;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.weaviate.WeaviateVectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class Base55Service {

    private final AgentTasksGeneratorService agentTasksGeneratorService;
    private final WeaviateVectorStore weaviateVectorStore;
    List<McpSyncClient> mcpSyncClients;

    public Base55Service(AgentTasksGeneratorService agentTasksGeneratorService, WeaviateVectorStore weaviateVectorStore,
                         List<McpSyncClient> mcpSyncClients) {
        this.agentTasksGeneratorService = agentTasksGeneratorService;
        this.weaviateVectorStore = weaviateVectorStore;
        this.mcpSyncClients = mcpSyncClients;
    }

    public Workflow generateAgentTasks (String task) {
        return agentTasksGeneratorService.generateAgentTasks(task);
    }

    public List<McpSchema.Tool> getFilteredTools(String Task){
        Workflow workflow = this.agentTasksGeneratorService.generateAgentTasks(Task);
        this.storeInVectorDB();
        List<McpSchema.Tool> tools = new ArrayList<>();
        for(WorkflowStep step : workflow.WorkflowSteps()){
            tools.addAll(this.filterToolsFromDb(step));
        }
        return null;
    }

    private List<McpSchema.Tool> filterToolsFromDb(WorkflowStep step) {
        String query = step.task(); // textual description of the workflow step

        // Build a search request
        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(10)                   // top N candidates
                .similarityThresholdAll()   // accept all first, we filter manually
                .build();

        // Search vector DB
        List<Document> candidates = weaviateVectorStore.doSimilaritySearch(request);

        double threshold = 0.75; // filter threshold
        int minResults = 3;      // safety net

        List<McpSchema.Tool> matchedTools = new ArrayList<>();

        for (Document doc : candidates) {
            double score = doc.getScore(); // certainty field from Weaviate
            if (score >= threshold) {
                String toolJson = (String) doc.getMetadata().get("toolJson");
                McpSchema.Tool tool = parseTool(toolJson);
                matchedTools.add(tool);
            }
        }

        // Ensure at least minResults returned
        if (matchedTools.size() < minResults && !candidates.isEmpty()) {
            for (int i = 0; i < Math.min(minResults, candidates.size()); i++) {
                String toolJson = (String) candidates.get(i).getMetadata().get("toolJson");
                matchedTools.add(parseTool(toolJson));
            }
        }

        return matchedTools;
    }

    private McpSchema.Tool parseTool(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, McpSchema.Tool.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Tool JSON", e);
        }
    }

    private void storeInVectorDB() {
        log.info("Storing tools in Weaviate Vector Store");
        for(McpSyncClient client : mcpSyncClients) {
            McpSchema.ListToolsResult listToolsResult = client.listTools();
            log.info("Storing {} tools in Weaviate Vector Store", listToolsResult.tools().size());
            for(McpSchema.Tool tool : listToolsResult.tools()) {
                Document doc = Document.builder()
                        .id(tool.name())
                        .text(tool.description())
                        .metadata("tool", tool)
                        .build();

                weaviateVectorStore.doAdd(List.of(doc));
            }
        }
    }
}
