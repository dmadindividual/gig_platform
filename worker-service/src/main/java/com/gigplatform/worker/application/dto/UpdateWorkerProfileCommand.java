package com.gigplatform.worker.application.dto;

import com.gigplatform.worker.domain.model.ExperienceLevel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record UpdateWorkerProfileCommand(
        @Size(max = 1000, message = "Bio cannot exceed 1000 characters")
        String bio,

        List<String> skills,

        @DecimalMin(value = "0.0", message = "Hourly rate must be positive")
        BigDecimal hourlyRate,

        ExperienceLevel experienceLevel,

        String portfolioUrl,

        String locationCity,

        String locationCountry,

        Double locationLat,

        Double locationLng
) {
}