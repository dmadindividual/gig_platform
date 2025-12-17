package com.gigplatform.shared.exception;

import java.util.UUID;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resourceType, UUID id) {
        super(String.format("%s with id %s not found", resourceType, id), "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String resourceType, String identifier) {
        super(String.format("%s with identifier %s not found", resourceType, identifier), "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND");
    }
}