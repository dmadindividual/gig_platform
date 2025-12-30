package com.gigplatform.user.domain.model;

import com.gigplatform.shared.domain.BaseEntity;
import com.gigplatform.shared.domain.Email;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    private Email email;
    private String passwordHash;
    private UserType userType;
    private UserStatus status;
    private boolean emailVerified;

    private String bio;
    private String phoneNumber;
    private String profileImageUrl;

    public static User create(String email, String passwordHash, UserType userType) {
        User user = new User();
        user.email = new Email(email);
        user.passwordHash = passwordHash;
        user.userType = userType;
        user.status = UserStatus.ACTIVE;
        user.emailVerified = false;
        return user;
    }

    public static User reconstitute(
            UUID id,
            String email,
            String passwordHash,
            UserType userType,
            UserStatus status,
            boolean emailVerified,
            String bio,                 // ADD
            String phoneNumber,         // ADD
            String profileImageUrl,     // ADD
            Instant createdAt,
            Instant updatedAt,
            Long version
    ) {
        User user = new User();
        user.setId(id);
        user.email = new Email(email);
        user.passwordHash = passwordHash;
        user.userType = userType;
        user.status = status;
        user.emailVerified = emailVerified;
        user.bio = bio;                          // ADD
        user.phoneNumber = phoneNumber;          // ADD
        user.profileImageUrl = profileImageUrl;  // ADD
        user.setCreatedAt(createdAt);
        user.setUpdatedAt(updatedAt);
        user.setVersion(version);
        return user;
    }
    public void verifyEmail() {
        if (this.emailVerified) {
            throw new IllegalStateException("Email already verified");
        }
        this.emailVerified = true;
    }

    public void deactivate() {
        if (this.status == UserStatus.DEACTIVATED) {
            throw new IllegalStateException("User already deactivated");
        }
        this.status = UserStatus.DEACTIVATED;
    }

    public void activate() {
        if (this.status == UserStatus.ACTIVE) {
            throw new IllegalStateException("User already active");
        }
        this.status = UserStatus.ACTIVE;
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    public void updateProfile(String bio, String phoneNumber, String profileImageUrl) {
        if (bio != null) {
            this.bio = bio;
        }
        if (phoneNumber != null) {
            this.phoneNumber = phoneNumber;
        }
        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
    }
}