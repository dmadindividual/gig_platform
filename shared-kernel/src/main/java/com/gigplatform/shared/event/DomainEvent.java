package com.gigplatform.shared.event;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public abstract class DomainEvent {

    private final UUID eventId;
    private final Instant occurredAt;
    private final String eventType;

    protected DomainEvent() {
        this.eventId = UUID.randomUUID();
        this.occurredAt = Instant.now();
        this.eventType = this.getClass().getSimpleName();
    }

    protected DomainEvent(UUID eventId, Instant occurredAt, String eventType) {
        this.eventId = eventId;
        this.occurredAt = occurredAt;
        this.eventType = eventType;
    }
}