package com.akatsuki.base55.controller;

import com.akatsuki.base55.domain.SubTask;
import com.akatsuki.base55.domain.mcp.tools.McpToolSpec;
import com.akatsuki.base55.domain.workflow.Workflow;
import com.akatsuki.base55.dto.AiRequestDTO;
import com.akatsuki.base55.exception.Base55Exception;
import com.akatsuki.base55.exception.ToolNotFoundException;
import com.akatsuki.base55.service.Base55Service;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/base55")
public class Base55Controller {

    private final Base55Service base55Service;
    @Autowired
    ToolCallbackProvider toolCallbackProvider;

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

    @GetMapping("/tools")
    public ToolCallback[] getAllTools() {
        return toolCallbackProvider.getToolCallbacks();
    }

    @PostMapping("/create-agent")
    public Map<String, Object> createAgent(@RequestBody String task) throws Base55Exception {
        return base55Service.createAgent(task);
    }
}