package com.gigplatform.user.domain.model;

import com.gigplatform.shared.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerificationToken extends BaseEntity {

    private UUID userId;
    private String token;
    private Instant expiryDate;

    public static EmailVerificationToken create(UUID userId, String token, Instant expiryDate) {
        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.userId = userId;
        verificationToken.token = token;
        verificationToken.expiryDate = expiryDate;
        return verificationToken;
    }

    public static EmailVerificationToken reconstitute(
            UUID id,
            UUID userId,
            String token,
            Instant expiryDate,
            Instant createdAt,
            Instant updatedAt,
            Long version
    ) {
        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setId(id);
        verificationToken.userId = userId;
        verificationToken.token = token;
        verificationToken.expiryDate = expiryDate;
        verificationToken.setCreatedAt(createdAt);
        verificationToken.setUpdatedAt(updatedAt);
        verificationToken.setVersion(version);
        return verificationToken;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiryDate);
    }

    public boolean isValid() {
        return !isExpired();
    }
}