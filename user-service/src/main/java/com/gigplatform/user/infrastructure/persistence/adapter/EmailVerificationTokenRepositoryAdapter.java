package com.gigplatform.user.infrastructure.persistence.adapter;

import com.gigplatform.user.domain.model.EmailVerificationToken;
import com.gigplatform.user.domain.repository.EmailVerificationTokenRepository;
import com.gigplatform.user.infrastructure.persistence.entity.EmailVerificationTokenJpaEntity;
import com.gigplatform.user.infrastructure.persistence.repository.EmailVerificationTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EmailVerificationTokenRepositoryAdapter implements EmailVerificationTokenRepository {

    private final EmailVerificationTokenJpaRepository jpaRepository;

    @Override
    public EmailVerificationToken save(EmailVerificationToken token) {
        EmailVerificationTokenJpaEntity entity = toEntity(token);
        EmailVerificationTokenJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<EmailVerificationToken> findByToken(String token) {
        return jpaRepository.findByToken(token).map(this::toDomain);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        jpaRepository.deleteByUserId(userId);
    }

    @Override
    public void delete(EmailVerificationToken token) {
        jpaRepository.deleteById(token.getId());
    }

    private EmailVerificationTokenJpaEntity toEntity(EmailVerificationToken token) {
        EmailVerificationTokenJpaEntity entity = new EmailVerificationTokenJpaEntity();
        entity.setId(token.getId());
        entity.setUserId(token.getUserId());
        entity.setToken(token.getToken());
        entity.setExpiryDate(token.getExpiryDate());
        entity.setCreatedAt(token.getCreatedAt());
        entity.setUpdatedAt(token.getUpdatedAt());
        entity.setVersion(token.getVersion());
        return entity;
    }

    private EmailVerificationToken toDomain(EmailVerificationTokenJpaEntity entity) {
        return EmailVerificationToken.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getToken(),
                entity.getExpiryDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }
}