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

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "agent_tool",
            joinColumns = @JoinColumn(name = "agent_id"),
            inverseJoinColumns = @JoinColumn(name = "tool_id")
    )
    private List<McpToolSpecEntity> mcpToolSpecs;

    @Builder
    public AiAgentConfigEntity(AiAgentMetadataEntity metadata, List<McpToolSpecEntity> mcpToolSpecs) {
        this.metadata = metadata;
        this.mcpToolSpecs = mcpToolSpecs;
    }
}
