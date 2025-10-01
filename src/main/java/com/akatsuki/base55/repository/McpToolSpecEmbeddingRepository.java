package com.akatsuki.base55.repository;

import com.akatsuki.base55.entity.McpToolSpecEmbeddingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface McpToolSpecEmbeddingRepository extends JpaRepository<McpToolSpecEmbeddingEntity, UUID> {
    boolean existsByToolSpecId(UUID toolSpecId);
}
