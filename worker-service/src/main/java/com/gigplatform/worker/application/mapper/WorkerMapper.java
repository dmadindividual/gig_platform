package com.gigplatform.worker.application.mapper;

import com.gigplatform.worker.application.dto.WorkerResponseDTO;
import com.gigplatform.worker.domain.model.Worker;
import org.springframework.stereotype.Component;

@Component
public class WorkerMapper {

    public WorkerResponseDTO toDTO(Worker worker) {
        return new WorkerResponseDTO(
                worker.getId(),
                worker.getUserId(),
                worker.getFirstName(),
                worker.getLastName(),
                worker.getBio(),
                worker.getSkills(),
                worker.getHourlyRate(),
                worker.getExperienceLevel(),
                worker.getAvailabilityStatus(),
                worker.getPortfolioUrl(),
                worker.getLocationCity(),
                worker.getLocationCountry(),
                worker.getTotalEarnings(),
                worker.getCompletedGigs(),
                worker.getAvgRating(),
                worker.getTotalReviews(),
                worker.getCreatedAt()
        );
    }
}