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
    private final ToolCallbackProvider toolCallbackProvider;
// switch back to reasoningChatClient
    public McpToolFilteringService(@Qualifier("executorChatClient") ChatClient groqChatClient,
                                   ToolCallbackProvider toolCallbackProvider) {
        this.groqChatClient = groqChatClient;
        this.toolCallbackProvider = toolCallbackProvider;
    }

    public List<McpToolSpec> getListOfToolsForWorkflowStep(WorkflowStep step, List<McpToolSpec> mcpToolSpecs) throws ToolNotFoundException {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an AI assistant. For the following workflow steps, analyze all the available tools and decide for each tool:\n\n");
        prompt.append("- Step: ").append(step.task()).append("\n");

        prompt.append("\nAvailable tools:\n");

        mcpToolSpecs.stream().forEach(tool -> {
            log.debug("Tool: {}", tool);
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
                                .param("tools", getToolDTOList(mcpToolSpecs))
                )
                .call()
                .entity(ToolEvaluation[].class);

        log.debug("LLM Response: {}", response);
        return convertToolEvaluationToMcpToolSpec(Arrays.stream(response).toList(), mcpToolSpecs);
    }

    private List<McpToolSpecDTO> getToolDTOList(List<McpToolSpec> mcpToolSpecs) {
        return mcpToolSpecs.stream()
                .map(tool -> new McpToolSpecDTO(
                        tool.id(),
                        tool.name(),
                        tool.description()
                ))
                .toList();
    }

    private List<McpToolSpec> convertToolEvaluationToMcpToolSpec(List<ToolEvaluation> toolEvaluations, List<McpToolSpec> mcpToolSpecs)
            throws ToolNotFoundException {
        List<McpToolSpec> requiredMcpTools = new ArrayList<>();
        for (ToolEvaluation toolEvaluation : toolEvaluations) {
            if (toolEvaluation.isRequired()) {
                requiredMcpTools.add(getMcpToolSpecById(toolEvaluation.id(), mcpToolSpecs) );
            }
        }
        return requiredMcpTools;
    }

    private McpToolSpec getMcpToolSpecById(UUID id, List<McpToolSpec> mcpToolSpecs) throws ToolNotFoundException {
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
