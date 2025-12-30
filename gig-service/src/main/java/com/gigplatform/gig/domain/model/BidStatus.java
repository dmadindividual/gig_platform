package com.gigplatform.gig.domain.model;

public enum BidStatus {
    PENDING,     // Submitted, awaiting client review
    ACCEPTED,    // Client accepted this bid
    REJECTED,    // Client rejected this bid
    WITHDRAWN    // Worker withdrew their bid
}