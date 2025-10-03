package com.akatsuki.base55.repository;

import com.akatsuki.base55.entity.McpToolSpecEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface McpToolSpecRepository extends JpaRepository<McpToolSpecEntity, UUID> {
    Optional<McpToolSpecEntity> findByServerNameAndName(String serverName, String name);
    Optional<McpToolSpecEntity> findByToolId(UUID toolId);
    List<McpToolSpecEntity> findAllByToolIdIn(List<UUID> ids);
}
