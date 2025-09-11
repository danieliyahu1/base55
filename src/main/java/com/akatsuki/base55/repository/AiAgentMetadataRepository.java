package com.akatsuki.base55.repository;

import com.akatsuki.base55.entity.AiAgentMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AiAgentMetadataRepository extends JpaRepository<AiAgentMetadataEntity, UUID> {
}
