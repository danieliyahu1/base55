package com.akatsuki.base55.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "mcp_tool_spec")
@NoArgsConstructor
@Getter
public class McpToolSpecEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "server_name", nullable = false)
    private String serverName;

    @Column(name = "tool_name", nullable = false)
    private String name;

    @Column(name = "description, length = 1024")
    private String description;

    @ManyToMany(mappedBy = "mcpToolSpecs")
    private List<AiAgentConfigEntity> agents;

    @Builder
    public McpToolSpecEntity(String serverName, String name, String description) {
        this.serverName = serverName;
        this.name = name;
        this.description = description;
    }
}
