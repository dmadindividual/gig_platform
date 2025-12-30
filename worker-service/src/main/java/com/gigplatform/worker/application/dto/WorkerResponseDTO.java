package com.gigplatform.worker.application.dto;

import com.gigplatform.worker.domain.model.AvailabilityStatus;
import com.gigplatform.worker.domain.model.ExperienceLevel;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record WorkerResponseDTO(
        UUID id,
        UUID userId,
        String firstName,
        String lastName,
        String bio,
        List<String> skills,
        BigDecimal hourlyRate,
        ExperienceLevel experienceLevel,
        AvailabilityStatus availabilityStatus,
        String portfolioUrl,
        String locationCity,
        String locationCountry,
        BigDecimal totalEarnings,
        Integer completedGigs,
        BigDecimal avgRating,
        Integer totalReviews,
        Instant createdAt
) {
}