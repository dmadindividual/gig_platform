package com.gigplatform.worker.domain.model;

import com.gigplatform.shared.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Worker extends BaseEntity {

    private UUID userId;  // Reference to user in user-service
    private String firstName;
    private String lastName;
    private String bio;
    private List<String> skills;
    private BigDecimal hourlyRate;
    private ExperienceLevel experienceLevel;
    private AvailabilityStatus availabilityStatus;
    private String portfolioUrl;
    private String locationCity;
    private String locationCountry;
    private Double locationLat;
    private Double locationLng;
    private BigDecimal totalEarnings;
    private Integer completedGigs;
    private BigDecimal avgRating;
    private Integer totalReviews;

    // Factory method - Create from UserRegisteredEvent
    public static Worker createFromUserRegistration(
            UUID userId,
            String firstName,
            String lastName
    ) {
        Worker worker = new Worker();
        worker.userId = userId;
        worker.firstName = firstName;
        worker.lastName = lastName;
        worker.skills = new ArrayList<>();
        worker.hourlyRate = BigDecimal.ZERO;
        worker.experienceLevel = ExperienceLevel.ENTRY_LEVEL;
        worker.availabilityStatus = AvailabilityStatus.AVAILABLE;
        worker.totalEarnings = BigDecimal.ZERO;
        worker.completedGigs = 0;
        worker.avgRating = BigDecimal.ZERO;
        worker.totalReviews = 0;
        return worker;
    }

    // Factory method - Reconstitute from database
    public static Worker reconstitute(
            UUID id,
            UUID userId,
            String firstName,
            String lastName,
            String bio,
            List<String> skills,
            BigDecimal hourlyRate,
            ExperienceLevel experienceLevel,
            AvailabilityStatus availabilityStatus,
            String portfolioUrl,
            String locationCity,
            String locationCountry,
            Double locationLat,
            Double locationLng,
            BigDecimal totalEarnings,
            Integer completedGigs,
            BigDecimal avgRating,
            Integer totalReviews,
            Instant createdAt,
            Instant updatedAt,
            Long version
    ) {
        Worker worker = new Worker();
        worker.setId(id);
        worker.userId = userId;
        worker.firstName = firstName;
        worker.lastName = lastName;
        worker.bio = bio;
        worker.skills = skills != null ? new ArrayList<>(skills) : new ArrayList<>();
        worker.hourlyRate = hourlyRate;
        worker.experienceLevel = experienceLevel;
        worker.availabilityStatus = availabilityStatus;
        worker.portfolioUrl = portfolioUrl;
        worker.locationCity = locationCity;
        worker.locationCountry = locationCountry;
        worker.locationLat = locationLat;
        worker.locationLng = locationLng;
        worker.totalEarnings = totalEarnings;
        worker.completedGigs = completedGigs;
        worker.avgRating = avgRating;
        worker.totalReviews = totalReviews;
        worker.setCreatedAt(createdAt);
        worker.setUpdatedAt(updatedAt);
        worker.setVersion(version);
        return worker;
    }

    // Business logic - Update profile
    public void updateProfile(
            String bio,
            List<String> skills,
            BigDecimal hourlyRate,
            ExperienceLevel experienceLevel,
            String portfolioUrl,
            String locationCity,
            String locationCountry,
            Double locationLat,
            Double locationLng
    ) {
        if (bio != null) this.bio = bio;
        if (skills != null) this.skills = new ArrayList<>(skills);
        if (hourlyRate != null && hourlyRate.compareTo(BigDecimal.ZERO) > 0) {
            this.hourlyRate = hourlyRate;
        }
        if (experienceLevel != null) this.experienceLevel = experienceLevel;
        if (portfolioUrl != null) this.portfolioUrl = portfolioUrl;
        if (locationCity != null) this.locationCity = locationCity;
        if (locationCountry != null) this.locationCountry = locationCountry;
        if (locationLat != null) this.locationLat = locationLat;
        if (locationLng != null) this.locationLng = locationLng;
    }

    // Business logic - Change availability
    public void changeAvailability(AvailabilityStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Availability status cannot be null");
        }
        this.availabilityStatus = newStatus;
    }

    // Business logic - Update stats after gig completion
    public void updateStatsAfterGigCompletion(BigDecimal earnings, BigDecimal rating) {
        this.completedGigs++;
        this.totalEarnings = this.totalEarnings.add(earnings);

        if (rating != null) {
            // Calculate new average rating
            BigDecimal totalRating = this.avgRating.multiply(BigDecimal.valueOf(this.totalReviews));
            totalRating = totalRating.add(rating);
            this.totalReviews++;
            this.avgRating = totalRating.divide(
                    BigDecimal.valueOf(this.totalReviews),
                    2,
                    BigDecimal.ROUND_HALF_UP
            );
        }
    }

    public boolean isAvailable() {
        return this.availabilityStatus == AvailabilityStatus.AVAILABLE;
    }
}