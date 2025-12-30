package com.gigplatform.user.infrastructure.messaging;

import com.gigplatform.shared.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private static final String USER_EVENTS_TOPIC = "user-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(DomainEvent event) {
        log.info("Publishing event: {} to topic: {}", event.getEventType(), USER_EVENTS_TOPIC);

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(USER_EVENTS_TOPIC, event.getEventId().toString(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Event published successfully: {} to partition: {}, offset: {}",
                        event.getEventType(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish event: {}", event.getEventType(), ex);
                // In production: retry logic, dead letter queue, alerting
            }
        });
    }

    public void publishSync(DomainEvent event) {
        try {
            log.info("Publishing event synchronously: {}", event.getEventType());
            SendResult<String, Object> result =
                    kafkaTemplate.send(USER_EVENTS_TOPIC, event.getEventId().toString(), event).get();

            log.info("Event published: {} to partition: {}, offset: {}",
                    event.getEventType(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
        } catch (Exception e) {
            log.error("Failed to publish event synchronously: {}", event.getEventType(), e);
            throw new RuntimeException("Event publishing failed", e);
        }
    }
}