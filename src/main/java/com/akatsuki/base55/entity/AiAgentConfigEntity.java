package com.akatsuki.base55.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ai_agent_config")
@NoArgsConstructor
@Getter
public class AiAgentConfigEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "metadata_id", referencedColumnName = "id")
    private AiAgentMetadataEntity metadata;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "agent_tool_ids",
            joinColumns = @JoinColumn(name = "agent_id")
    )
    @Column(name = "tool_id", nullable = false)
    private List<UUID> mcpToolSpecIds;

    @Builder
    public AiAgentConfigEntity(AiAgentMetadataEntity metadata, List<UUID> mcpToolSpecIds) {
        this.metadata = metadata;
        this.mcpToolSpecIds = mcpToolSpecIds;
    }
}
