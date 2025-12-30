package com.gigplatform.gig.domain.model;

public enum GigStatus {
    DRAFT,          // Client is still editing
    OPEN,           // Published, accepting bids
    ASSIGNED,       // Worker assigned, not started yet
    IN_PROGRESS,    // Worker actively working
    COMPLETED,      // Work delivered and approved
    CANCELLED,      // Client cancelled
    DISPUTED        // Under dispute resolution
}