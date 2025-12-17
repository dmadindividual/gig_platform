package com.gigplatform.shared.exception;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends BusinessException {

    private final Map<String, String> fieldErrors;

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
        this.fieldErrors = new HashMap<>();
    }

    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(message, "VALIDATION_ERROR");
        this.fieldErrors = fieldErrors;
    }

    public ValidationException(String field, String error) {
        super("Validation failed", "VALIDATION_ERROR");
        this.fieldErrors = new HashMap<>();
        this.fieldErrors.put(field, error);
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}