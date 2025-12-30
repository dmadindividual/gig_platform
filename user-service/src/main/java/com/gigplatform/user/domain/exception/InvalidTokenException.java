package com.gigplatform.user.domain.exception;

import com.gigplatform.shared.exception.BusinessException;

public class InvalidTokenException extends BusinessException {
    public InvalidTokenException() {
        super("Invalid or expired token", "INVALID_TOKEN");
    }
}