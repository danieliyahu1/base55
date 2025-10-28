package com.akatsuki.base55.repository;

import com.akatsuki.base55.entity.AiAgentConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AiAgentConfigRepository extends JpaRepository<AiAgentConfigEntity, UUID>{
    Optional<AiAgentConfigEntity> findByMetadata_Id(UUID metadataId);
}
