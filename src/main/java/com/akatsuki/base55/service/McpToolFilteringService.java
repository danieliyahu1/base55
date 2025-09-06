package com.akatsuki.base55.service;

import com.akatsuki.base55.domain.McpToolSpec;
import com.akatsuki.base55.domain.ToolEvaluation;
import com.akatsuki.base55.domain.workflow.Workflow;
import com.akatsuki.base55.domain.workflow.step.WorkflowStep;
import com.akatsuki.base55.dto.McpToolSpecDTO;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.akatsuki.base55.constant.orchestrationConstants.TOOL_FILTERING_LLM_ROLE;
import static com.akatsuki.base55.constant.orchestrationConstants.TOOL_FILTERING_TASK_DESCRIPTION;

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

    public Map<String, List<McpToolSpec>> getFilteredTools(Workflow workflow) {
        log.info("Filtering tools for workflow with {} steps", workflow.WorkflowSteps().size());
        log.info("Using MCP clients: {}", mcpSyncClients.stream()
                .map(McpSyncClient::getServerInfo));
        Map<String, List<McpToolSpec>> toolsByStep = workflow.WorkflowSteps().stream()
                .peek(step -> log.info("Processing workflow step: {}", step.task()))
                .collect(Collectors.toMap(
                        WorkflowStep::task,
                        this::getListOfToolsForWorkflowStep
                ));
        return toolsByStep;
    }

    private List<McpToolSpec> getListOfToolsForWorkflowStep(WorkflowStep step){
        // 2️⃣ Build the LLM prompt
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an AI assistant. For the following workflow steps, analyze all the available tools and decide for each tool:\n\n");
        prompt.append("- Step: ").append(step.task()).append("\n");

        prompt.append("\nAvailable tools:\n");

        mcpToolSpecs.stream().forEach(tool -> {
            log.info("Tool: {}", tool);
            prompt.append("Id: ").append(tool.id()).append("\n");
            prompt.append("- Name: ").append(tool.name()).append("\n");
            prompt.append("  Description: ").append(tool.description()).append("\n");
        });

        // 3️⃣ Call the ChatClient (LLM)
        ToolEvaluation[] response = groqChatClient
                .prompt(prompt.toString())
                .system(s -> s.text(TOOL_FILTERING_LLM_ROLE))
                .user(u ->
                        u.text(TOOL_FILTERING_TASK_DESCRIPTION)
                                .param("step", step.task())
                                .param("tools", getToolDTOList())
                )
                .call()
                .entity(ToolEvaluation[].class);

        log.info("LLM Response: {}", response);
        return convertToolEvaluationToMcpToolSpec(Arrays.stream(response).toList());
    }

    private List<McpToolSpec> getAllToolsFromAllMcpClients(){
        return mcpSyncClients.stream()
                .flatMap(mcpSyncClient -> mcpSyncClient.listTools().tools().stream(
                ).map(tool -> new McpToolSpec(tool.name(), tool.description(), mcpSyncClient.getServerInfo().name())
                ))
                .collect(Collectors.toList());
    }

    private List<McpToolSpecDTO> getToolDTOList(){
        return mcpToolSpecs.stream()
                .map(tool -> new McpToolSpecDTO(
                        tool.id(),
                        tool.name(),
                        tool.description()
                ))
                .collect(Collectors.toList());
    }

    private List<McpToolSpec> convertToolEvaluationToMcpToolSpec(List<ToolEvaluation> toolEvaluations){
        return toolEvaluations.stream()
                .filter(ToolEvaluation::isRequired)
                .map(toolEvaluation -> new McpToolSpec(
                        getToolNameById(toolEvaluation.id()),
                        toolEvaluation.rationale(),
                        getServerNameById(toolEvaluation.id()
                )))
                .collect(Collectors.toList());
    }

    private String getToolNameById(UUID id){
        return mcpToolSpecs.stream()
                .filter(tool -> tool.id().equals(id))
                .findFirst()
                .map(McpToolSpec::name)
                .orElseThrow(); //********************************needs to create custom exception
    }

    private String getServerNameById(UUID id){
        return mcpToolSpecs.stream()
                .filter(tool -> tool.id().equals(id))
                .findFirst()
                .map(McpToolSpec::serverName)
                .orElseThrow(); //********************************needs to create custom exception
    }
}
