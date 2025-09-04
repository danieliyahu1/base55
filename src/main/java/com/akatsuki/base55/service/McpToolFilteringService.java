package com.akatsuki.base55.service;

import com.akatsuki.base55.domain.McpToolSpec;
import com.akatsuki.base55.domain.ToolEvaluation;
import com.akatsuki.base55.domain.workflow.Workflow;
import com.akatsuki.base55.domain.workflow.step.WorkflowStep;
import com.fasterxml.classmate.GenericType;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class McpToolFilteringService {

    private final ChatClient groqChatClient;
    private final List<McpSyncClient> mcpSyncClients;
    private final List<McpToolSpec> mcpToolSpecs;
    public McpToolFilteringService(@Qualifier("groqChatClient") ChatClient groqChatClient,
                                   List<McpSyncClient> mcpSyncClients) {
        this.groqChatClient = groqChatClient;
        this.mcpSyncClients = mcpSyncClients;
        this.mcpToolSpecs = this.getAllToolsFromAllMcpClients();
    }

    public List<McpSchema.Tool> getFilteredTools(Workflow workflow) {
        log.info("Filtering tools for workflow with {} steps", workflow.WorkflowSteps().size());
        log.info("Using MCP clients: {}", mcpSyncClients.stream()
                .map(McpSyncClient::getServerInfo));
        for(WorkflowStep step : workflow.WorkflowSteps()){
            log.info("Processing workflow step: {}", step.task());
            List<McpSchema.Tool> toolsForStep = getListOfToolsForWorkflowStep(step);
        }
        return null;
    }

    private List<McpSchema.Tool> getListOfToolsForWorkflowStep(WorkflowStep step){
        // 2️⃣ Build the LLM prompt
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an AI assistant. For the following workflow steps, analyze all the available tools and decide for each tool:\n\n");
        prompt.append("- Step: ").append(step.task()).append("\n");

        prompt.append("\nAvailable tools:\n");
        for (McpToolSpec tool : mcpToolSpecs) {
            prompt.append("Id: ").append(tool.id()).append("\n");
            prompt.append("- Name: ").append(tool.name()).append("\n");
            prompt.append("  Description: ").append(tool.description()).append("\n");
        }

        // 3️⃣ Call the ChatClient (LLM)
        ToolEvaluation[] response = groqChatClient
                .prompt(prompt.toString())
                .call()
                .entity(ToolEvaluation[].class);

        log.info("LLM Response: {}", response);
        return null;
    }

    private List<McpToolSpec> getAllToolsFromAllMcpClients(){
        return mcpSyncClients.stream()
                .flatMap(mcpSyncClient -> mcpSyncClient.listTools().tools().stream(
                ).map(tool -> new McpToolSpec(tool.name(), tool.description(), mcpSyncClient.getServerInfo().name())
                ))
                .collect(Collectors.toList());
    }
}
