package com.gigplatform.shared.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@NoArgsConstructor  // For Jackson deserialization
public class UserRegisteredEvent extends DomainEvent {

    private UUID userId;
    private String email;
    private String userType;

    public UserRegisteredEvent(UUID userId, String email, String userType) {
        super();
        this.userId = userId;
        this.email = email;
        this.userType = userType;
    }

    // Constructor for deserialization with all fields
    public UserRegisteredEvent(
            UUID eventId,
            Instant occurredAt,
            String eventType,
            UUID userId,
            String email,
            String userType
    ) {
        super(eventId, occurredAt, eventType);
        this.userId = userId;
        this.email = email;
        this.userType = userType;
    }
}