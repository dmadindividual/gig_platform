package com.gigplatform.user.domain.repository;

import com.gigplatform.user.domain.model.EmailVerificationToken;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationTokenRepository {

    EmailVerificationToken save(EmailVerificationToken token);

    Optional<EmailVerificationToken> findByToken(String token);

    void deleteByUserId(UUID userId);

    void delete(EmailVerificationToken token);
}