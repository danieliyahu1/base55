package com.akatsuki.base55.service;

import com.akatsuki.base55.domain.AiResponseDomain;
import com.akatsuki.base55.domain.UserPromptIntent;
import com.akatsuki.base55.domain.workflow.Workflow;
import com.akatsuki.base55.domain.workflow.step.WorkflowStep;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class McpToolFilteringService {

    private final ChatClient groqChatClient;
    private final List<McpSyncClient> mcpSyncClients;
    public McpToolFilteringService(@Qualifier("groqChatClient") ChatClient groqChatClient,
                                   List<McpSyncClient> mcpSyncClients) {
        this.groqChatClient = groqChatClient;
        this.mcpSyncClients = mcpSyncClients;
    }

    public List<McpSchema.Tool> getFilteredTools(Workflow workflow) {
        log.info("Filtering tools for workflow with {} steps", workflow.WorkflowSteps().size());
        log.info("Using MCP clients: {}", mcpSyncClients.stream()
                .map(McpSyncClient::getServerInfo));
        for(WorkflowStep step : workflow.WorkflowSteps()){
            log.info("Processing workflow step: {}", step.task());
            List<McpSchema.Tool> toolsForStep = getListOfToolsForWorkflowStep(step);
            log.info("Tools for step '{}': {}", step.task(), toolsForStep);
        }
        return null;
    }

    private List<McpSchema.Tool> getListOfToolsForWorkflowStep(WorkflowStep step){
        // 2️⃣ Build the LLM prompt
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an AI assistant. For the following workflow steps, analyze all the available tools and decide for each tool:\n\n");
        prompt.append("- Step: ").append(step.task()).append("\n");

        prompt.append("\nAvailable tools:\n");
        for (McpSchema.Tool tool : getAllToolsFromAllMcpClients()) {
            prompt.append("- Name: ").append(tool.name()).append("\n");
            prompt.append("  Description: ").append(tool.description()).append("\n");
        }

        prompt.append("\nFor each tool, return JSON with fields: tool_name, needed (true/false), reason.\n");

        // 3️⃣ Call the ChatClient (LLM)
        ChatClient.CallResponseSpec response = groqChatClient
                .prompt(prompt.toString())
                .call();

        log.info("LLM Response: {}", response.content());
        return null;
    }

    private List<McpSchema.Tool> getAllToolsFromAllMcpClients(){
        return mcpSyncClients.stream()
                .flatMap(mcpSyncClient -> mcpSyncClient.listTools().tools().stream())
                .collect(Collectors.toList());
    }
}
