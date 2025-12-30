package com.gigplatform.worker.domain.model;

public enum AvailabilityStatus {
    AVAILABLE,    // Ready to take new gigs
    BUSY,         // Currently working on gigs
    OFFLINE,      // Not accepting gigs
    AWAY          // Temporarily unavailable
}