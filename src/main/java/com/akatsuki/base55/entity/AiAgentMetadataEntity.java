package com.akatsuki.base55.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.UUID;

@Entity
@Table(name = "ai_agent_metadata")
@NoArgsConstructor
@Getter
public class AiAgentMetadataEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "agent_id", unique = true, nullable = false)
    private UUID agentId;

    @Column(name = "agent_system_prompt", nullable = false)
    private String agentSystemPrompt;

    @Builder
    public AiAgentMetadataEntity(@NonNull String description, @NonNull UUID agentId, @NonNull String agentSystemPrompt) {
        this.description = description;
        this.agentId = agentId;
        this.agentSystemPrompt = agentSystemPrompt;
    }
}