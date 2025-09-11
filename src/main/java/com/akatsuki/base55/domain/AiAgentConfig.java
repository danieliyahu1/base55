package com.akatsuki.base55.domain;

import org.springframework.ai.tool.ToolCallbackProvider;

public record AiAgentConfig(String agentDescription, ToolCallbackProvider toolCallbackProvider) {
}