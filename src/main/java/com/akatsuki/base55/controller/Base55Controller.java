package com.akatsuki.base55.controller;

import com.akatsuki.base55.domain.agent.AiAgentMetadata;
import com.akatsuki.base55.domain.agent.TaskExecutorResponse;
import com.akatsuki.base55.domain.mcp.tools.McpToolSpec;
import com.akatsuki.base55.domain.workflow.Workflow;
import com.akatsuki.base55.dto.AiRequestDTO;
import com.akatsuki.base55.entity.AiAgentConfigEntity;
import com.akatsuki.base55.entity.AiAgentMetadataEntity;
import com.akatsuki.base55.exception.AgentNotFound;
import com.akatsuki.base55.exception.Base55Exception;
import com.akatsuki.base55.exception.ToolNotFoundException;
import com.akatsuki.base55.repository.AiAgentConfigRepository;
import com.akatsuki.base55.repository.AiAgentMetadataRepository;
import com.akatsuki.base55.service.Base55Service;
import com.akatsuki.base55.service.ToolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static com.akatsuki.base55.constant.AgentConstants.SYSTEM_PROMPT_TASK_EXECUTOR;
import static com.akatsuki.base55.constant.AgentConstants.USER_PROMPT_SUB_TASK_EXECUTOR;

@RestController
@RequestMapping("/api/v1/base55")
@Slf4j
public class Base55Controller {

    private final Base55Service base55Service;
    @Autowired
    ToolCallbackProvider toolCallbackProvider;
    @Autowired
    ToolService toolService;
    @Autowired
    @Qualifier("testChatClient")
    ChatClient testChatClient;
    @Autowired
    ChatMemory chatMemory;
    @Autowired
    AiAgentConfigRepository aiAgentConfigRepository;
    @Autowired
    AiAgentMetadataRepository aiAgentMetadataRepository;

    public Base55Controller(Base55Service base55Service) {
        this.base55Service = base55Service;
    }

    @PostMapping("/generate-workflow")
    public Workflow generateAgentWorkflow(@RequestBody AiRequestDTO request) {
        return base55Service.generateAgentWorkflow(request.getPrompt());
    }

    @PostMapping("/filter-tools")
    public List<McpToolSpec> filterTools(@RequestBody AiRequestDTO request) throws ToolNotFoundException, Base55Exception {
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

    @PostMapping("/agents2/{id}")
    public TaskExecutorResponse executeTask(@PathVariable String id, @RequestBody AiRequestDTO request) throws AgentNotFound {
        return base55Service.executeTask(id, request.getPrompt());
    }

    @PostMapping("/testChatClient")
    public ChatResponse testGroqChatClient(@RequestBody AiRequestDTO request) throws AgentNotFound {
        log.info("Received request to check the function call on ChatClient");
        String task = request.getPrompt();
        return this.testChatClient.prompt()
                .system(s -> s.text(SYSTEM_PROMPT_TASK_EXECUTOR))
                .user(u -> u.text(USER_PROMPT_SUB_TASK_EXECUTOR)
                        .param("sub-task", task))
                .call()
                .chatResponse();
    }

    @PostMapping("/testChatClient2")
    public ChatClientResponse testGroqChatClient2(@RequestBody AiRequestDTO request) throws AgentNotFound {
        log.info("Received request to check the function call on ChatClient2");
        String task = request.getPrompt();
        return this.testChatClient.prompt()
                .system(s -> s.text(SYSTEM_PROMPT_TASK_EXECUTOR))
                .user(u -> u.text(USER_PROMPT_SUB_TASK_EXECUTOR)
                        .param("sub-task", task))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, "testChatClient2"))
                .call()
                .chatClientResponse();
    }

    @DeleteMapping("/testChatClient2/memory/clear")
    public List<Message> clearMemory(){
        chatMemory.clear("testChatClient2");
        return chatMemory.get("testChatClient2");
    }

    @GetMapping("/testChatClient2/memory/get")
    public List<Message> getMemory(){
        return chatMemory.get("testChatClient2");
    }

    @GetMapping("/agents")
    public List<AiAgentMetadata> getAllAgent() {
        return base55Service.getAllAgents();
    }

    @PostMapping("/similar-tools/{topK}")
        public List<Document> getSimilarTools(@RequestBody String query, @PathVariable int topK) {
        return toolService.getSimilarToolsByQueryAndTopK2(query, topK);
    }

    @GetMapping("agents/{agentId}/tools")
    public List<McpToolSpec> getAgentTools(@PathVariable String agentId) throws AgentNotFound {
        return base55Service.getAgentTools(agentId);
    }

    @GetMapping("/metadata")
    public List<AiAgentMetadataEntity> aiAgentMetadataEntities(){
        return aiAgentMetadataRepository.findAll();
    }

    @GetMapping("/metadata/{agentId}")
    public Optional<AiAgentMetadataEntity> aiAgentMetadataEntities(@PathVariable String agentId){
        return aiAgentMetadataRepository.findByAgentId(UUID.fromString(agentId));
    }

    @GetMapping("/configs")
    public List<AiAgentConfigEntity> aiAgentConfigEntities(){
        return aiAgentConfigRepository.findAll();
    }

    @GetMapping("/configs/{metadataId}")
    public Optional<AiAgentConfigEntity> aiAgentConfigEntities(@PathVariable String metadataId){
        return aiAgentConfigRepository.findByMetadata_Id(UUID.fromString(metadataId));
    }
}