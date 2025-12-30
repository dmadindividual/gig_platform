package com.gigplatform.worker.infrastructure.persistence.adapter;

import com.gigplatform.worker.domain.model.Worker;
import com.gigplatform.worker.domain.repository.WorkerRepository;
import com.gigplatform.worker.infrastructure.persistence.entity.WorkerJpaEntity;
import com.gigplatform.worker.infrastructure.persistence.repository.WorkerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WorkerRepositoryAdapter implements WorkerRepository {

    private final WorkerJpaRepository jpaRepository;

    @Override
    public Worker save(Worker worker) {
        WorkerJpaEntity entity = toEntity(worker);
        WorkerJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Worker> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Worker> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return jpaRepository.existsByUserId(userId);
    }

    @Override
    public void delete(Worker worker) {
        jpaRepository.deleteById(worker.getId());
    }

    private WorkerJpaEntity toEntity(Worker worker) {
        WorkerJpaEntity entity = new WorkerJpaEntity();
        entity.setId(worker.getId());
        entity.setUserId(worker.getUserId());
        entity.setFirstName(worker.getFirstName());
        entity.setLastName(worker.getLastName());
        entity.setBio(worker.getBio());
        entity.setSkills(worker.getSkills().toArray(new String[0]));
        entity.setHourlyRate(worker.getHourlyRate());
        entity.setExperienceLevel(worker.getExperienceLevel());
        entity.setAvailabilityStatus(worker.getAvailabilityStatus());
        entity.setPortfolioUrl(worker.getPortfolioUrl());
        entity.setLocationCity(worker.getLocationCity());
        entity.setLocationCountry(worker.getLocationCountry());
        entity.setLocationLat(worker.getLocationLat());
        entity.setLocationLng(worker.getLocationLng());
        entity.setTotalEarnings(worker.getTotalEarnings());
        entity.setCompletedGigs(worker.getCompletedGigs());
        entity.setAvgRating(worker.getAvgRating());
        entity.setTotalReviews(worker.getTotalReviews());
        entity.setCreatedAt(worker.getCreatedAt());
        entity.setUpdatedAt(worker.getUpdatedAt());
        entity.setVersion(worker.getVersion());
        return entity;
    }

    private Worker toDomain(WorkerJpaEntity entity) {
        List<String> skills = entity.getSkills() != null
                ? Arrays.asList(entity.getSkills())
                : List.of();

        return Worker.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getBio(),
                skills,
                entity.getHourlyRate(),
                entity.getExperienceLevel(),
                entity.getAvailabilityStatus(),
                entity.getPortfolioUrl(),
                entity.getLocationCity(),
                entity.getLocationCountry(),
                entity.getLocationLat(),
                entity.getLocationLng(),
                entity.getTotalEarnings(),
                entity.getCompletedGigs(),
                entity.getAvgRating(),
                entity.getTotalReviews(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }
}