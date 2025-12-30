package com.gigplatform.user.domain.model;

import com.gigplatform.shared.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordResetToken extends BaseEntity {

    private UUID userId;
    private String token;
    private Instant expiryDate;
    private boolean used;

    public static PasswordResetToken create(UUID userId, String token, Instant expiryDate) {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.userId = userId;
        resetToken.token = token;
        resetToken.expiryDate = expiryDate;
        resetToken.used = false;
        return resetToken;
    }

    public static PasswordResetToken reconstitute(
            UUID id,
            UUID userId,
            String token,
            Instant expiryDate,
            boolean used,
            Instant createdAt,
            Instant updatedAt,
            Long version
    ) {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setId(id);
        resetToken.userId = userId;
        resetToken.token = token;
        resetToken.expiryDate = expiryDate;
        resetToken.used = used;
        resetToken.setCreatedAt(createdAt);
        resetToken.setUpdatedAt(updatedAt);
        resetToken.setVersion(version);
        return resetToken;
    }

    public void markAsUsed() {
        if (this.used) {
            throw new IllegalStateException("Token already used");
        }
        this.used = true;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiryDate);
    }

    public boolean isValid() {
        return !this.used && !isExpired();
    }
}