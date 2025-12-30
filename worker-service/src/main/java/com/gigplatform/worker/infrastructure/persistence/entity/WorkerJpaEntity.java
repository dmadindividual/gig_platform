package com.gigplatform.worker.infrastructure.persistence.entity;

import com.gigplatform.shared.domain.BaseEntity;
import com.gigplatform.worker.domain.model.AvailabilityStatus;
import com.gigplatform.worker.domain.model.ExperienceLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "workers",
        indexes = {
                @Index(name = "idx_workers_user_id", columnList = "user_id"),
                @Index(name = "idx_workers_availability", columnList = "availability_status"),
                @Index(name = "idx_workers_location", columnList = "location_lat, location_lng")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkerJpaEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "bio", length = 1000)
    private String bio;

    @Column(name = "skills")
    private String[] skills;  // PostgreSQL array

    @Column(name = "hourly_rate", precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", length = 20)
    private ExperienceLevel experienceLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", nullable = false, length = 20)
    private AvailabilityStatus availabilityStatus;

    @Column(name = "portfolio_url", length = 500)
    private String portfolioUrl;

    @Column(name = "location_city", length = 100)
    private String locationCity;

    @Column(name = "location_country", length = 100)
    private String locationCountry;

    @Column(name = "location_lat")
    private Double locationLat;

    @Column(name = "location_lng")
    private Double locationLng;

    @Column(name = "total_earnings", precision = 12, scale = 2)
    private BigDecimal totalEarnings;

    @Column(name = "completed_gigs")
    private Integer completedGigs;

    @Column(name = "avg_rating", precision = 3, scale = 2)
    private BigDecimal avgRating;

    @Column(name = "total_reviews")
    private Integer totalReviews;
}