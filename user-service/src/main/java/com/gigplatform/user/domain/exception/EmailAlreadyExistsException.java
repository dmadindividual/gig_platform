package com.gigplatform.user.domain.exception;

import com.gigplatform.shared.exception.BusinessException;

public class EmailAlreadyExistsException extends BusinessException {

    public EmailAlreadyExistsException(String email) {
        super(String.format("User with email '%s' already exists", email), "EMAIL_ALREADY_EXISTS");
    }
}