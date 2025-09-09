package com.akatsuki.base55.service;

import com.akatsuki.base55.domain.McpToolSpec;
import com.akatsuki.base55.domain.ToolEvaluation;
import com.akatsuki.base55.domain.workflow.Workflow;
import com.akatsuki.base55.domain.workflow.step.WorkflowStep;
import com.akatsuki.base55.dto.McpToolSpecDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.akatsuki.base55.constant.OrchestrationConstants.TOOL_FILTERING_LLM_ROLE;
import static com.akatsuki.base55.constant.OrchestrationConstants.TOOL_FILTERING_TASK_DESCRIPTION;

@Slf4j
@Service
public class McpToolFilteringService {

    private final ChatClient groqChatClient;
    private final List<McpToolSpec> mcpToolSpecs;
    private final ToolCallbackProvider toolCallbackProvider;
    public McpToolFilteringService(@Qualifier("groqChatClient") ChatClient groqChatClient,
                                   ToolCallbackProvider toolCallbackProvider) {
        this.groqChatClient = groqChatClient;
        this.toolCallbackProvider = toolCallbackProvider;
        this.mcpToolSpecs = this.getAllToolsFromToolCallbackProvider();
    }

    public ToolCallbackProvider getFilteredCallBackTools(Workflow workflow) {
        log.info("Filtering tools for workflow with {} steps", workflow.WorkflowSteps().size());

        List<McpToolSpec> filteredMcpToolSpecs =  workflow.WorkflowSteps().stream()
                .flatMap(step -> getListOfToolsForWorkflowStep(step).stream())
                .distinct()
                .toList();

        return createToolCallbackProviderFromMcpToolSpecs(filteredMcpToolSpecs);
    }

    private List<McpToolSpec> getListOfToolsForWorkflowStep(WorkflowStep step){
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

    private List<McpToolSpec> getAllToolsFromToolCallbackProvider(){
        return Arrays.stream(toolCallbackProvider.getToolCallbacks()).map(tool -> new McpToolSpec(
                tool.getToolDefinition().name(),
                tool.getToolDefinition().description()
        )).toList();
    }

    private List<McpToolSpecDTO> getToolDTOList(){
        return mcpToolSpecs.stream()
                .map(tool -> new McpToolSpecDTO(
                        tool.id(),
                        tool.name(),
                        tool.description()
                ))
                .toList();
    }

    private List<McpToolSpec> convertToolEvaluationToMcpToolSpec(List<ToolEvaluation> toolEvaluations){
        return toolEvaluations.stream()
                .filter(ToolEvaluation::isRequired)
                .map(toolEvaluation -> new McpToolSpec(
                        getToolNameById(toolEvaluation.id()),
                        getToolDescriptionById(toolEvaluation.id())
                ))
                .toList();
    }

    private String getToolDescriptionById(UUID id) {
        return mcpToolSpecs.stream()
                .filter(tool -> tool.id().equals(id))
                .findFirst()
                .map(McpToolSpec::description)
                .orElseThrow(); //********************************needs to create custom exception
    }

    private String getToolNameById(UUID id){
        return mcpToolSpecs.stream()
                .filter(tool -> tool.id().equals(id))
                .findFirst()
                .map(McpToolSpec::name)
                .orElseThrow(); //********************************needs to create custom exception
    }

    private ToolCallbackProvider createToolCallbackProviderFromMcpToolSpecs(List<McpToolSpec> mcpToolSpec){
        return ToolCallbackProvider.from(
                Arrays.stream(toolCallbackProvider.getToolCallbacks()).filter(
                                tool -> mcpToolSpec.stream().anyMatch(spec -> spec.name().equals(tool.getToolDefinition().name())
                                        && spec.description().equals(tool.getToolDefinition().description()))
                        )
                        .toList()
        );
    }
}
