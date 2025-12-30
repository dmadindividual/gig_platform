package com.gigplatform.worker.domain.repository;

import com.gigplatform.worker.domain.model.Worker;

import java.util.Optional;
import java.util.UUID;

public interface WorkerRepository {

    Worker save(Worker worker);

    Optional<Worker> findById(UUID id);

    Optional<Worker> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    void delete(Worker worker);
}