package com.gigplatform.user.application.dto;

import com.gigplatform.user.domain.model.UserStatus;
import com.gigplatform.user.domain.model.UserType;

import java.time.Instant;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String email,
        UserType userType,
        UserStatus status,
        boolean emailVerified,
        String bio,                  // ADD
        String phoneNumber,          // ADD
        String profileImageUrl,
        Instant createdAt
) {
}