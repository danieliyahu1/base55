package com.akatsuki.base55.domain.mcp.tools;

import java.util.UUID;

public record McpToolSpec(
        UUID id,
        String name,
        String description) {
    public McpToolSpec(String name, String description) {
        this(UUID.randomUUID(), name, description);
    }
}