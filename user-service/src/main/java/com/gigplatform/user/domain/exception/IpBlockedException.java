package com.gigplatform.user.domain.exception;

import com.gigplatform.shared.exception.BusinessException;

public class IpBlockedException extends BusinessException {
    public IpBlockedException(String message) {
        super(message, "IP_BLOCKED");
    }

    public IpBlockedException() {
        super("Access denied. Your IP address has been blocked.", "IP_BLOCKED");
    }
}