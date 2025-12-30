package com.gigplatform.worker.presentation.rest;

import com.gigplatform.shared.dto.ApiResponse;
import com.gigplatform.worker.application.dto.UpdateWorkerProfileCommand;
import com.gigplatform.worker.application.dto.WorkerResponseDTO;
import com.gigplatform.worker.application.service.WorkerApplicationService;
import com.gigplatform.worker.domain.model.AvailabilityStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/workers")
@RequiredArgsConstructor
@Slf4j
public class WorkerController {

    private final WorkerApplicationService workerApplicationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<WorkerResponseDTO>> getWorkerByUserId(
            @PathVariable UUID userId
    ) {
        log.info("Request to get worker profile for userId: {}", userId);

        WorkerResponseDTO worker = workerApplicationService.getWorkerByUserId(userId);

        return ResponseEntity.ok(ApiResponse.success(worker));
    }

    @PutMapping("/user/{userId}/profile")
    public ResponseEntity<ApiResponse<WorkerResponseDTO>> updateWorkerProfile(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateWorkerProfileCommand command
    ) {
        log.info("Request to update worker profile for userId: {}", userId);

        WorkerResponseDTO updatedWorker = workerApplicationService.updateWorkerProfile(userId, command);

        return ResponseEntity.ok(ApiResponse.success(updatedWorker, "Profile updated successfully"));
    }

    @PutMapping("/user/{userId}/availability")
    public ResponseEntity<ApiResponse<WorkerResponseDTO>> changeAvailability(
            @PathVariable UUID userId,
            @RequestParam AvailabilityStatus status
    ) {
        log.info("Request to change availability for userId: {} to {}", userId, status);

        WorkerResponseDTO updatedWorker = workerApplicationService.changeAvailability(userId, status);

        return ResponseEntity.ok(ApiResponse.success(updatedWorker, "Availability updated successfully"));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Worker service is healthy"));
    }
}