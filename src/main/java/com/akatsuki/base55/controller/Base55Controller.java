package com.akatsuki.base55.controller;

import com.akatsuki.base55.domain.McpToolSpec;
import com.akatsuki.base55.domain.workflow.Workflow;
import com.akatsuki.base55.dto.AiRequestDTO;
import com.akatsuki.base55.dto.AiResponseDTO;
import com.akatsuki.base55.service.Base55Service;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/base55")
public class Base55Controller {

    private final Base55Service base55Service;

    public Base55Controller(Base55Service base55Service) {
        this.base55Service = base55Service;
    }

    @PostMapping("/generate-tasks")
    public Workflow generateAgentTasks(@RequestBody AiRequestDTO request){
        return base55Service.generateAgentTasks(request.getPrompt());
    }

    @PostMapping("/filter-tools")
    public Map<String, List<McpToolSpec>> filterTools(@RequestBody AiRequestDTO request){
        return base55Service.getFilteredTools(request.getPrompt());
    }
}
