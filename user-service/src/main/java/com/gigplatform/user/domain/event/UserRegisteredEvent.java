package com.gigplatform.user.domain.event;

import com.gigplatform.shared.event.DomainEvent;
import com.gigplatform.user.domain.model.UserType;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString(callSuper = true)
public class UserRegisteredEvent extends DomainEvent {

    private final UUID userId;
    private final String email;
    private final UserType userType;

    public UserRegisteredEvent(UUID userId, String email, UserType userType) {
        super();  // Generates eventId and timestamp
        this.userId = userId;
        this.email = email;
        this.userType = userType;
    }
}