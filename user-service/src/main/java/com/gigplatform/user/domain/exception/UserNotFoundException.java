package com.gigplatform.user.domain.exception;

import com.gigplatform.shared.exception.ResourceNotFoundException;

import java.util.UUID;

public class UserNotFoundException extends ResourceNotFoundException {

    public UserNotFoundException(UUID userId) {
        super("User", userId);
    }

    public UserNotFoundException(String email) {
        super("User", email);
    }
}