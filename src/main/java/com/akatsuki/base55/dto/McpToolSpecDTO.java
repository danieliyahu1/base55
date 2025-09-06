package com.akatsuki.base55.dto;

import java.util.UUID;

public record McpToolSpecDTO(
        UUID id,
        String name,
        String description
) { }
