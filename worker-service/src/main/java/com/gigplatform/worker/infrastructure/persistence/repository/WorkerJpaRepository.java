package com.gigplatform.worker.infrastructure.persistence.repository;

import com.gigplatform.worker.infrastructure.persistence.entity.WorkerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkerJpaRepository extends JpaRepository<WorkerJpaEntity, UUID> {

    Optional<WorkerJpaEntity> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}