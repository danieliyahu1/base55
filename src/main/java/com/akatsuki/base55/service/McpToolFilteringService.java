package com.akatsuki.base55.service;

import com.akatsuki.base55.domain.UserPromptIntent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class McpToolFilteringService {

    private final ChatClient groqChatClient;

    public McpToolFilteringService(@Qualifier("groqChatClient") ChatClient groqChatClient) {
        this.groqChatClient = groqChatClient;
    }

    public static List<ToolCallback> getFilteredTools(ToolCallbackProvider toolCallbackProvider, UserPromptIntent userPromptIntent) {
        return null;
//        return toolCallbackProvider.getToolCallbacks().stream()
//                .filter(tool -> isToolIncluded(tool, prompt))
//                .collect(Collectors.toList());
    }

//    private ToolCallback isRelevantTool(ToolCallback tool, String prompt){
//
//    }
}
