package com.akatsuki.base55.controller;

import com.akatsuki.base55.domain.mcp.tools.McpToolSpec;
import com.akatsuki.base55.domain.workflow.Workflow;
import com.akatsuki.base55.dto.AiRequestDTO;
import com.akatsuki.base55.dto.AiResponseDTO;
import com.akatsuki.base55.exception.AgentNotFound;
import com.akatsuki.base55.exception.Base55Exception;
import com.akatsuki.base55.exception.ToolNotFoundException;
import com.akatsuki.base55.service.Base55Service;
import com.akatsuki.base55.service.ToolService;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/base55")
public class Base55Controller {

    private final Base55Service base55Service;
    @Autowired
    ToolCallbackProvider toolCallbackProvider;
    @Autowired
    ToolService toolService;

    public Base55Controller(Base55Service base55Service) {
        this.base55Service = base55Service;
    }

    @PostMapping("/generate-workflow")
    public Workflow generateAgentWorkflow(@RequestBody AiRequestDTO request) {
        return base55Service.generateAgentWorkflow(request.getPrompt());
    }

    @PostMapping("/filter-tools")
    public List<McpToolSpec> filterTools(@RequestBody AiRequestDTO request) throws ToolNotFoundException {
        return base55Service.getFilteredTools(request.getPrompt());
    }

    @PostMapping("/create-agent")
    public Map<String, Object> createAgent(@RequestBody String task) throws Base55Exception {
        return base55Service.createAgent(task);
    }

    @GetMapping("/tool-callbacks")
    public List<ToolDefinition> getAllToolCallbacks() {
        return Stream.of(toolCallbackProvider.getToolCallbacks()).map(ToolCallback::getToolDefinition).toList();
    }

    @GetMapping("/mcp-tool-specs")
    public List<McpToolSpec> getAllMcpToolSpecs() {
        return toolService.getAllMcpToolSpecs();
    }

    @PostMapping("/mcp-tool-spec-embeddings")
    public List<Document> getAllMcpToolSpecEmbeddings(@RequestBody Map<String, Object> requestBody) {

        // 1. Extract the String 'query'
        String query = (String) requestBody.get("query");

        // 2. Extract the Integer 'topK'.
        //    Note: JSON numbers often map to Integer by default.
        Integer topKObject = (Integer) requestBody.get("topK");

        // Handle potential null or casting issues (important for robustness)
        if (query == null || topKObject == null) {
            // Throw an appropriate exception, e.g., Bad Request (400)
            throw new IllegalArgumentException("Missing required parameters: query or topK");
        }

        int topK = topKObject; // Autounboxing

        return toolService.getSimilarDocumentsByQueryAndTopK(query, topK);
    }

    @GetMapping("/mcp-tool-spec-embeddings/{id}")
    public Document getMcpToolSpecEmbeddingById(@PathVariable String id) {
        return toolService.getToolById(id);
    }

    @PostMapping("/agent/{id}")
    public AiResponseDTO chatWithAgent(@PathVariable String id, @RequestBody AiRequestDTO request) throws AgentNotFound {
        return new AiResponseDTO(base55Service.chatWithAgent(id, request.getPrompt()).result());
    }
}