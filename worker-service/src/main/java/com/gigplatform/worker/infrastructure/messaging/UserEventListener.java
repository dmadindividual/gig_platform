package com.gigplatform.worker.infrastructure.messaging;

import com.gigplatform.shared.event.UserRegisteredEvent;
import com.gigplatform.worker.application.service.WorkerApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventListener {

    private final WorkerApplicationService workerApplicationService;

    @KafkaListener(
            topics = "user-events",
            groupId = "worker-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserEvent(
            @Payload UserRegisteredEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment
    ) {
        log.info("Received UserRegisteredEvent: userId={}, email={}, userType={}, partition={}, offset={}",
                event.getUserId(), event.getEmail(), event.getUserType(), partition, offset);

        try {
            // Only create worker profile if user type is WORKER
            if ("WORKER".equals(event.getUserType())) {
                workerApplicationService.createWorkerFromUserRegistration(event);
                log.info("Worker profile created successfully for userId: {}", event.getUserId());
            } else {
                log.debug("Ignoring event for non-WORKER user type: {}", event.getUserType());
            }

            // Manually acknowledge message processing
            acknowledgment.acknowledge();
            log.debug("Message acknowledged: partition={}, offset={}", partition, offset);

        } catch (Exception e) {
            log.error("Error processing UserRegisteredEvent for userId: {}", event.getUserId(), e);
            // Don't acknowledge - message will be reprocessed
            throw e;
        }
    }
}