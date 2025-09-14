package com.akatsuki.base55.domain.agent;

import com.akatsuki.base55.domain.mcp.tools.McpToolSpec;

import java.util.List;

public record AiAgentConfig(AiAgentMetadata metadata, List<McpToolSpec> mcpToolSpecs) {
}