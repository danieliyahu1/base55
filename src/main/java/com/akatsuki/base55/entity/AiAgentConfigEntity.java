package com.akatsuki.base55.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;

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

    @Builder
    public AiAgentConfigEntity(AiAgentMetadataEntity metadata) {
        this.metadata = metadata;
    }
}
