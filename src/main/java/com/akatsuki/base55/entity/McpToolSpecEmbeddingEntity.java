package com.akatsuki.base55.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "mcp_tool_spec_embedding")
@NoArgsConstructor
@Getter
public class McpToolSpecEmbeddingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "tool_spec_id", nullable = false, unique = true)
    private UUID toolSpecId;

    @Column(name = "embedding", columnDefinition = "vector(384)", nullable = false)
    @JdbcTypeCode(SqlTypes.VECTOR)
    // Use @Array to specify the dimension (optional, but good practice)
    @Array(length = 384)
    private float[] embedding;

    @Builder
    public McpToolSpecEmbeddingEntity(UUID toolSpecId, float[] embedding) {
        this.toolSpecId = toolSpecId;
        this.embedding = embedding;
    }
}
