package com.akatsuki.base55.domain.agent;

import lombok.NonNull;

import java.util.UUID;

public record AiAgentMetadata(@NonNull UUID id, @NonNull String description, @NonNull String agentSystemPrompt) {
}