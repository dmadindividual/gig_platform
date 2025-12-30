package com.gigplatform.gig.domain.model;

import com.gigplatform.shared.domain.BaseEntity;
import com.gigplatform.shared.domain.Money;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Gig extends BaseEntity {

    private UUID clientId;  // Reference to user in user-service
    private String title;
    private String description;
    private List<String> requiredSkills;
    private Money budgetMin;
    private Money budgetMax;
    private GigUrgency urgency;
    private GigStatus status;
    private boolean locationRequired;
    private String locationCity;
    private String locationCountry;
    private Double locationLat;
    private Double locationLng;
    private Instant deadline;
    private UUID assignedWorkerId;  // Worker who won the bid
    private Instant assignedAt;
    private Instant startedAt;
    private Instant completedAt;

    // Factory method - Create new gig
    public static Gig create(
            UUID clientId,
            String title,
            String description,
            List<String> requiredSkills,
            Money budgetMin,
            Money budgetMax,
            GigUrgency urgency,
            boolean locationRequired,
            String locationCity,
            String locationCountry,
            Double locationLat,
            Double locationLng,
            Instant deadline
    ) {
        Gig gig = new Gig();
        gig.clientId = clientId;
        gig.title = title;
        gig.description = description;
        gig.requiredSkills = requiredSkills != null ? new ArrayList<>(requiredSkills) : new ArrayList<>();
        gig.budgetMin = budgetMin;
        gig.budgetMax = budgetMax;
        gig.urgency = urgency;
        gig.status = GigStatus.DRAFT;
        gig.locationRequired = locationRequired;
        gig.locationCity = locationCity;
        gig.locationCountry = locationCountry;
        gig.locationLat = locationLat;
        gig.locationLng = locationLng;
        gig.deadline = deadline;

        gig.validate();
        return gig;
    }

    // Factory method - Reconstitute from database
    public static Gig reconstitute(
            UUID id,
            UUID clientId,
            String title,
            String description,
            List<String> requiredSkills,
            Money budgetMin,
            Money budgetMax,
            GigUrgency urgency,
            GigStatus status,
            boolean locationRequired,
            String locationCity,
            String locationCountry,
            Double locationLat,
            Double locationLng,
            Instant deadline,
            UUID assignedWorkerId,
            Instant assignedAt,
            Instant startedAt,
            Instant completedAt,
            Instant createdAt,
            Instant updatedAt,
            Long version
    ) {
        Gig gig = new Gig();
        gig.setId(id);
        gig.clientId = clientId;
        gig.title = title;
        gig.description = description;
        gig.requiredSkills = requiredSkills != null ? new ArrayList<>(requiredSkills) : new ArrayList<>();
        gig.budgetMin = budgetMin;
        gig.budgetMax = budgetMax;
        gig.urgency = urgency;
        gig.status = status;
        gig.locationRequired = locationRequired;
        gig.locationCity = locationCity;
        gig.locationCountry = locationCountry;
        gig.locationLat = locationLat;
        gig.locationLng = locationLng;
        gig.deadline = deadline;
        gig.assignedWorkerId = assignedWorkerId;
        gig.assignedAt = assignedAt;
        gig.startedAt = startedAt;
        gig.completedAt = completedAt;
        gig.setCreatedAt(createdAt);
        gig.setUpdatedAt(updatedAt);
        gig.setVersion(version);
        return gig;
    }

    // Business logic - Publish gig
    public void publish() {
        if (this.status != GigStatus.DRAFT) {
            throw new IllegalStateException("Can only publish draft gigs");
        }
        validate();
        this.status = GigStatus.OPEN;
    }

    // Business logic - Assign to worker
    public void assignToWorker(UUID workerId) {
        if (this.status != GigStatus.OPEN) {
            throw new IllegalStateException("Can only assign open gigs");
        }
        if (workerId == null) {
            throw new IllegalArgumentException("Worker ID cannot be null");
        }
        this.assignedWorkerId = workerId;
        this.assignedAt = Instant.now();
        this.status = GigStatus.ASSIGNED;
    }

    // Business logic - Start work
    public void startWork() {
        if (this.status != GigStatus.ASSIGNED) {
            throw new IllegalStateException("Can only start assigned gigs");
        }
        this.startedAt = Instant.now();
        this.status = GigStatus.IN_PROGRESS;
    }

    // Business logic - Complete work
    public void complete() {
        if (this.status != GigStatus.IN_PROGRESS) {
            throw new IllegalStateException("Can only complete in-progress gigs");
        }
        this.completedAt = Instant.now();
        this.status = GigStatus.COMPLETED;
    }

    // Business logic - Cancel gig
    public void cancel() {
        if (this.status == GigStatus.COMPLETED || this.status == GigStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel completed or already cancelled gigs");
        }
        if (this.status == GigStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot cancel in-progress gigs without dispute");
        }
        this.status = GigStatus.CANCELLED;
    }

    // Business logic - Mark as disputed
    public void dispute() {
        if (this.status != GigStatus.IN_PROGRESS && this.status != GigStatus.COMPLETED) {
            throw new IllegalStateException("Can only dispute in-progress or completed gigs");
        }
        this.status = GigStatus.DISPUTED;
    }

    // Business logic - Update gig details (only when draft or open)
    public void update(
            String title,
            String description,
            List<String> requiredSkills,
            Money budgetMin,
            Money budgetMax,
            GigUrgency urgency,
            Instant deadline
    ) {
        if (this.status != GigStatus.DRAFT && this.status != GigStatus.OPEN) {
            throw new IllegalStateException("Can only update draft or open gigs");
        }

        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (requiredSkills != null) this.requiredSkills = new ArrayList<>(requiredSkills);
        if (budgetMin != null) this.budgetMin = budgetMin;
        if (budgetMax != null) this.budgetMax = budgetMax;
        if (urgency != null) this.urgency = urgency;
        if (deadline != null) this.deadline = deadline;

        validate();
    }

    // Validation rules
    private void validate() {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }
        if (budgetMin != null && budgetMax != null) {
            if (budgetMin.isGreaterThan(budgetMax)) {
                throw new IllegalArgumentException("Minimum budget cannot exceed maximum budget");
            }
        }
        if (deadline != null && deadline.isBefore(Instant.now())) {
            throw new IllegalArgumentException("Deadline must be in the future");
        }
    }

    // Query methods
    public boolean isOpen() {
        return this.status == GigStatus.OPEN;
    }

    public boolean isAssigned() {
        return this.assignedWorkerId != null;
    }

    public boolean canAcceptBids() {
        return this.status == GigStatus.OPEN;
    }

    public boolean belongsToClient(UUID clientId) {
        return this.clientId.equals(clientId);
    }
}