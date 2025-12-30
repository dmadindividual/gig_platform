package com.gigplatform.user.application.command;

import jakarta.validation.constraints.Size;

public record UpdateUserProfileCommand(

        @Size(max = 500, message = "Bio cannot exceed 500 characters")
        String bio,

        String phoneNumber,

        String profileImageUrl
) {
}