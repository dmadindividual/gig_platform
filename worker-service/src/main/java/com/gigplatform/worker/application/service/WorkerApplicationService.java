package com.gigplatform.worker.application.service;

import com.gigplatform.shared.event.UserRegisteredEvent;
import com.gigplatform.shared.exception.ResourceNotFoundException;
import com.gigplatform.worker.application.dto.UpdateWorkerProfileCommand;
import com.gigplatform.worker.application.dto.WorkerResponseDTO;
import com.gigplatform.worker.application.mapper.WorkerMapper;
import com.gigplatform.worker.domain.model.AvailabilityStatus;
import com.gigplatform.worker.domain.model.Worker;
import com.gigplatform.worker.domain.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerApplicationService {

    private final WorkerRepository workerRepository;
    private final WorkerMapper workerMapper;

    @Transactional
    public void createWorkerFromUserRegistration(UserRegisteredEvent event) {
        log.info("Creating worker profile from user registration: userId={}", event.getUserId());

        // Check if worker already exists (idempotency)
        if (workerRepository.existsByUserId(event.getUserId())) {
            log.warn("Worker profile already exists for userId: {}", event.getUserId());
            return;
        }

        // Extract first/last name from email (temporary until user provides real names)
        String[] nameParts = event.getEmail().split("@")[0].split("\\.");
        String firstName = nameParts.length > 0 ? capitalize(nameParts[0]) : "Worker";
        String lastName = nameParts.length > 1 ? capitalize(nameParts[1]) : "User";

        // Create worker
        Worker worker = Worker.createFromUserRegistration(
                event.getUserId(),
                firstName,
                lastName
        );

        workerRepository.save(worker);
        log.info("Worker profile created successfully: workerId={}, userId={}",
                worker.getId(), event.getUserId());
    }

    @Transactional(readOnly = true)
    public WorkerResponseDTO getWorkerByUserId(UUID userId) {
        log.info("Fetching worker profile for userId: {}", userId);

        Worker worker = workerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", userId.toString()));

        return workerMapper.toDTO(worker);
    }

    @Transactional
    public WorkerResponseDTO updateWorkerProfile(UUID userId, UpdateWorkerProfileCommand command) {
        log.info("Updating worker profile for userId: {}", userId);

        Worker worker = workerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", userId.toString()));

        worker.updateProfile(
                command.bio(),
                command.skills(),
                command.hourlyRate(),
                command.experienceLevel(),
                command.portfolioUrl(),
                command.locationCity(),
                command.locationCountry(),
                command.locationLat(),
                command.locationLng()
        );

        Worker updatedWorker = workerRepository.save(worker);
        log.info("Worker profile updated successfully: workerId={}", updatedWorker.getId());

        return workerMapper.toDTO(updatedWorker);
    }

    @Transactional
    public WorkerResponseDTO changeAvailability(UUID userId, AvailabilityStatus newStatus) {
        log.info("Changing availability for userId: {} to {}", userId, newStatus);

        Worker worker = workerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Worker", userId.toString()));

        worker.changeAvailability(newStatus);
        Worker updatedWorker = workerRepository.save(worker);

        log.info("Availability changed successfully: workerId={}, newStatus={}",
                updatedWorker.getId(), newStatus);

        return workerMapper.toDTO(updatedWorker);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}