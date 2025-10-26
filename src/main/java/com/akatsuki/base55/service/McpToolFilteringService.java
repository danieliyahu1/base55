package com.akatsuki.base55.service;

import com.akatsuki.base55.domain.mcp.tools.McpToolSpec;
import com.akatsuki.base55.domain.mcp.tools.ToolEvaluation;
import com.akatsuki.base55.domain.workflow.step.WorkflowStep;
import com.akatsuki.base55.dto.McpToolSpecDTO;
import com.akatsuki.base55.exception.Base55Exception;
import com.akatsuki.base55.exception.ToolNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.akatsuki.base55.constant.PlatformConstants.TOOL_FILTERING_LLM_ROLE;
import static com.akatsuki.base55.constant.PlatformConstants.TOOL_FILTERING_TASK_DESCRIPTION;
import static com.akatsuki.base55.exception.constant.ExceptionConstant.TOOL_NOT_FOUND_EXCEPTION_MESSAGE;

@Slf4j
@Service
public class McpToolFilteringService {

    private final ChatClient groqChatClient;
    private final ToolCallbackProvider toolCallbackProvider;

    public McpToolFilteringService(@Qualifier("openAIChatClient") ChatClient groqChatClient,
                                   ToolCallbackProvider toolCallbackProvider) {
        this.groqChatClient = groqChatClient;
        this.toolCallbackProvider = toolCallbackProvider;
    }

    public List<McpToolSpec> getListOfToolsForWorkflowStep(WorkflowStep step, List<McpToolSpec> mcpToolSpecs) throws ToolNotFoundException, Base55Exception {
        log.info("Filtering tools for workflow step: {}", step);
        log.info("Tools name available for filtering: {}", mcpToolSpecs.stream().map(McpToolSpec::name).toList());
        log.info("Number of tools available for filtering: {}", mcpToolSpecs.size());
        ToolEvaluation[] filteredTools = groqChatClient
                .prompt()
                .system(s -> s.text(TOOL_FILTERING_LLM_ROLE))
                .user(u ->
                        u.text(TOOL_FILTERING_TASK_DESCRIPTION)
                                .param("step", step.task())
                                .param("tools", getToolDTOList(mcpToolSpecs))
                )
                .call()
                .entity(ToolEvaluation[].class);

        if(filteredTools == null){
            log.error("Tool filtering LLM returned null response");
            throw new Base55Exception("Tool filtering LLM returned null response");
        }
        log.info("LLM Response: {}", (Object)filteredTools);
        log.info("Number of tools before filtering: {}", filteredTools.length);
        log.info("Number of tools selected by LLM: {}", Arrays.stream(filteredTools).filter(ToolEvaluation::isRequired).count());
        return convertToolEvaluationToMcpToolSpec(Arrays.stream(filteredTools).toList(), mcpToolSpecs);
    }

    private List<McpToolSpecDTO> getToolDTOList(List<McpToolSpec> mcpToolSpecs) {
        return mcpToolSpecs.stream()
                .map(tool -> new McpToolSpecDTO(
                        tool.id(),
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
