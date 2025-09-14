package com.akatsuki.base55.domain.mcp.tools;

import java.util.UUID;

public record McpToolSpec(
        UUID id,
        String name,
        String description,
        String serverName) {
    public McpToolSpec(String name, String description, String serverName) {
        this(UUID.randomUUID(), name, description, serverName);
    }
}