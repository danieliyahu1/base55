package com.akatsuki.base55.domain.mcp.tools;

import java.util.UUID;

public record McpToolSpec(
        UUID id,
        String name,
        String description,
        String serverName) {
    public static McpToolSpec newSpec(String name, String description, String serverName) {
        return new McpToolSpec(UUID.randomUUID(), name, description, serverName);
    }

    public static McpToolSpec fromDb(UUID id, String name, String description, String serverName) {
        return new McpToolSpec(id, name, description, serverName);
    }
}