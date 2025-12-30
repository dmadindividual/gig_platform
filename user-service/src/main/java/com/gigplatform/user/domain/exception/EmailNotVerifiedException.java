package com.gigplatform.user.domain.exception;

import com.gigplatform.shared.exception.BusinessException;

public class EmailNotVerifiedException extends BusinessException {
    public EmailNotVerifiedException() {
        super("Email not verified. Please check your email for verification link.", "EMAIL_NOT_VERIFIED");
    }
}