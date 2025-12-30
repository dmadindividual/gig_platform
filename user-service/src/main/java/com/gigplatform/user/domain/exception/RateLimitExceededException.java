package com.gigplatform.user.domain.exception;

import com.gigplatform.shared.exception.BusinessException;

public class RateLimitExceededException extends BusinessException {
    public RateLimitExceededException(String message) {
        super(message, "RATE_LIMIT_EXCEEDED");
    }

    public RateLimitExceededException() {
        super("Too many requests. Please try again later.", "RATE_LIMIT_EXCEEDED");
    }
}