package com.akatsuki.base55.domain.agent;

import org.springframework.ai.tool.ToolCallback;

public record AiAgentConfig(AiAgentMetadata metadata, ToolCallback[] toolCallbacks) {
}