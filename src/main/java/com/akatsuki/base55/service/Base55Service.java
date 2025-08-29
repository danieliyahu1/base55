package com.akatsuki.base55.service;

import com.akatsuki.base55.dto.AiRequestDTO;
import com.akatsuki.base55.dto.AiResponseDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class Base55Service {

    private final ChatClient chatClient;
    AiOrchestratorService aiOrchestratorService;

    public Base55Service(@Qualifier("openAiChatClient") ChatClient chatClient, AiOrchestratorService aiOrchestratorService) {
        this.chatClient = chatClient;
        this.aiOrchestratorService = aiOrchestratorService;
    }

    public AiResponseDTO askLlm(AiRequestDTO request) {
        return new AiResponseDTO(this.chatClient.prompt(request.getPrompt()).tools().call().content());
    }

    public AiResponseDTO executeTask (String task) {
        return new AiResponseDTO(aiOrchestratorService.executeTask(task).result());
    }
}
