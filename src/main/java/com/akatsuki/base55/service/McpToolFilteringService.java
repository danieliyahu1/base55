package com.akatsuki.base55.service;

import com.akatsuki.base55.domain.mcp.tools.McpToolSpec;
import com.akatsuki.base55.domain.mcp.tools.ToolEvaluation;
import com.akatsuki.base55.domain.workflow.Workflow;
import com.akatsuki.base55.domain.workflow.step.WorkflowStep;
import com.akatsuki.base55.dto.McpToolSpecDTO;
import com.akatsuki.base55.exception.ToolNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.akatsuki.base55.constant.PlatformConstants.TOOL_FILTERING_LLM_ROLE;
import static com.akatsuki.base55.constant.PlatformConstants.TOOL_FILTERING_TASK_DESCRIPTION;
import static com.akatsuki.base55.exception.constant.ExceptionConstant.TOOL_NOT_FOUND_EXCEPTION_MESSAGE;

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

    public ToolCallbackProvider getFilteredCallBackTools(Workflow workflow) throws ToolNotFoundException {
        log.info("Filtering tools for workflow with {} steps", workflow.WorkflowSteps().size());

        List<McpToolSpec> filteredMcpToolSpecs = new ArrayList<>();
        Set<UUID> uniqueToolIds = new HashSet<>(); // to ensure distinct tools

        for (WorkflowStep step : workflow.WorkflowSteps()) {
            List<McpToolSpec> toolsForStep = getListOfToolsForWorkflowStep(step);
            for (McpToolSpec tool : toolsForStep) {
                if (uniqueToolIds.add(tool.id())) { // only add if not already added
                    filteredMcpToolSpecs.add(tool);
                }
            }
        }

        return createToolCallbackProviderFromMcpToolSpecs(filteredMcpToolSpecs);
    }

    private List<McpToolSpec> getListOfToolsForWorkflowStep(WorkflowStep step) throws ToolNotFoundException {
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

    private List<McpToolSpec> convertToolEvaluationToMcpToolSpec(List<ToolEvaluation> toolEvaluations)
            throws ToolNotFoundException {
        List<McpToolSpec> requiredMcpTools = new ArrayList<>();
        for (ToolEvaluation toolEvaluation : toolEvaluations) {
            if (toolEvaluation.isRequired()) {
                requiredMcpTools.add(getMcpToolSpecById(toolEvaluation.id()));
            }
        }
        return requiredMcpTools;
    }

    private McpToolSpec getMcpToolSpecById(UUID id) throws ToolNotFoundException {
        return mcpToolSpecs.stream()
                .filter(tool -> tool.id().equals(id))
                .findFirst()
                .orElseThrow(
                        () -> new ToolNotFoundException(String.format(TOOL_NOT_FOUND_EXCEPTION_MESSAGE, id))
                );
    }

    private ToolCallbackProvider createToolCallbackProviderFromMcpToolSpecs(List<McpToolSpec> mcpToolSpec){
        return ToolCallbackProvider.from(
                Arrays.stream(toolCallbackProvider.getToolCallbacks())
                        .filter(
                                tool -> mcpToolSpec.stream().anyMatch(spec -> spec.name().equals(tool.getToolDefinition().name())
                                        && spec.description().equals(tool.getToolDefinition().description()))
                        )
                        .toList()
        );
    }
}
