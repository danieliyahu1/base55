package com.akatsuki.base55.service;

import com.akatsuki.base55.dto.AiRequest;
import com.akatsuki.base55.dto.AiResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class Base55Service {

    private final ChatClient chatClient;

    public Base55Service(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public AiResponse askLlm(AiRequest request) {
        return new AiResponse(this.chatClient.prompt(request.getPrompt()).tools().call().content());
    }

    public String getTools() {
        return "tools";
    }
}
