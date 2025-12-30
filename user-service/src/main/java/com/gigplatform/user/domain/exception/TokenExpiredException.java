package com.gigplatform.user.domain.exception;

import com.gigplatform.shared.exception.BusinessException;

public class TokenExpiredException extends BusinessException {
    public TokenExpiredException() {
        super("Token has expired", "TOKEN_EXPIRED");
    }
}