package com.gigplatform.user.application.service;

import com.gigplatform.shared.exception.BusinessException;
import com.gigplatform.user.application.command.*;
import com.gigplatform.user.application.dto.AuthResponseDTO;
import com.gigplatform.user.application.dto.UserResponseDTO;
import com.gigplatform.user.application.mapper.UserMapper;
import com.gigplatform.user.domain.event.UserRegisteredEvent;
import com.gigplatform.user.domain.exception.*;
import com.gigplatform.user.domain.model.EmailVerificationToken;
import com.gigplatform.user.domain.model.PasswordResetToken;
import com.gigplatform.user.domain.model.User;
import com.gigplatform.user.domain.model.UserStatus;
import com.gigplatform.user.domain.repository.EmailVerificationTokenRepository;
import com.gigplatform.user.domain.repository.PasswordResetTokenRepository;
import com.gigplatform.user.domain.repository.UserRepository;
import com.gigplatform.user.infrastructure.messaging.EventPublisher;
import com.gigplatform.user.infrastructure.security.JwtService;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserApplicationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EventPublisher eventPublisher;
    private final UserMapper userMapper;
    private final Counter userRegistrationCounter;      // ADD
    private final Counter loginSuccessCounter;          // ADD
    private final Counter loginFailureCounter;
    private final PasswordResetTokenRepository resetTokenRepository;  // ADD THIS
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;  // ADD



    @Transactional
    public AuthResponseDTO registerUser(RegisterUserCommand command) {
        log.info("Registering new user with email: {}", command.email());

        if (userRepository.existsByEmail(command.email())) {
            log.warn("Registration attempt with existing email: {}", command.email());
            throw new EmailAlreadyExistsException(command.email());
        }
        String hashedPassword = passwordEncoder.encode(command.password());
        log.debug("Password hashed successfully");
        User user = User.create(command.email(), hashedPassword, command.userType());
        log.debug("User domain entity created: {}", user.getId());
        User savedUser = userRepository.save(user);
        String verificationToken = UUID.randomUUID().toString();
        EmailVerificationToken token = EmailVerificationToken.create(
                savedUser.getId(),
                verificationToken,
                Instant.now().plusSeconds(86400)  // 24 hours
        );
        emailVerificationTokenRepository.save(token);
        String verifyLink = "https://gigplatform.com/verify-email?token=" + verificationToken;
        log.info("Email verification link: {}", verifyLink);
        userRegistrationCounter.increment();  // ADD THIS

        log.info("User saved to database with ID: {}", savedUser.getId());
        UserRegisteredEvent event = new UserRegisteredEvent(
                savedUser.getId(),
                savedUser.getEmail().getValue(),
                savedUser.getUserType()
        );
        eventPublisher.publish(event);
        log.debug("UserRegisteredEvent published for user: {}", savedUser.getId());
        String accessToken = jwtService.generateToken(
                savedUser.getId(),
                savedUser.getEmail().getValue(),
                savedUser.getUserType().name()
        );
        String refreshToken = jwtService.generateRefreshToken(
                savedUser.getId(),
                savedUser.getEmail().getValue()
        );
        log.debug("JWT tokens generated for user: {}", savedUser.getId());
        UserResponseDTO userDTO = userMapper.toDTO(savedUser);

        return new AuthResponseDTO(
                accessToken,
                refreshToken,
                86400,
                userDTO
        );
    }


    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginCommand command) {
      try {
          log.info("Login attempt for email: {}", command.email());
          User user = userRepository.findByEmail(command.email())
                  .orElseThrow(() -> {
                      log.warn("Login failed: User not found for email: {}", command.email());
                      throw new InvalidCredentialsException();
                  });

          if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
              log.warn("Login failed: Invalid password for email: {}", command.email());
              throw new InvalidCredentialsException();
          }

          if (!user.isActive()) {
              log.warn("Login failed: User account is not active for email: {}", command.email());
              throw new BusinessException("Account is not active", "ACCOUNT_INACTIVE");
          }
          if (!user.isEmailVerified()) {
              log.warn("Login attempt with unverified email: {}", command.email());
              throw new EmailNotVerifiedException();
          }

          log.info("Login successful for user: {}", user.getId());

          String accessToken = jwtService.generateToken(
                  user.getId(),
                  user.getEmail().getValue(),
                  user.getUserType().name()
          );
          String refreshToken = jwtService.generateRefreshToken(
                  user.getId(),
                  user.getEmail().getValue()
          );
          UserResponseDTO userDTO = userMapper.toDTO(user);
          loginSuccessCounter.increment();  // ADD THIS


          return new AuthResponseDTO(
                  accessToken,
                  refreshToken,
                  86400, // 24 hours
                  userDTO
          );

      } catch (InvalidCredentialsException e) {
          loginFailureCounter.increment();  // ADD THIS
          throw e;
      }

    }


    @Transactional(readOnly = true)
    public AuthResponseDTO refreshToken(RefreshTokenCommand command) {
        log.info("Token refresh request received");

        // 1. Validate refresh token
        if (!jwtService.isTokenValid(command.refreshToken())) {
            log.warn("Invalid refresh token");
            throw new InvalidCredentialsException();
        }

        // 2. Extract user info from refresh token
        UUID userId = jwtService.extractUserId(command.refreshToken());
        String email = jwtService.extractEmail(command.refreshToken());

        // 3. Verify user still exists and is active
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found for token refresh: {}", userId);
                    throw new UserNotFoundException(userId);
                });

        if (!user.isActive()) {
            log.warn("Token refresh failed: User account is not active: {}", userId);
            throw new BusinessException("Account is not active", "ACCOUNT_INACTIVE");
        }

        log.info("Generating new tokens for user: {}", userId);

        // 4. Generate NEW tokens (token rotation)
        String newAccessToken = jwtService.generateToken(
                user.getId(),
                user.getEmail().getValue(),
                user.getUserType().name()
        );

        String newRefreshToken = jwtService.generateRefreshToken(
                user.getId(),
                user.getEmail().getValue()
        );

        // 5. Return new tokens
        UserResponseDTO userDTO = userMapper.toDTO(user);

        return new AuthResponseDTO(
                newAccessToken,
                newRefreshToken,
                86400,
                userDTO
        );
    }

    @Transactional
    public UserResponseDTO updateUserProfile(UUID userId, UpdateUserProfileCommand command) {
        log.info("Updating profile for user: {}", userId);

        // 1. Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 2. Update profile (domain logic)
        user.updateProfile(
                command.bio(),
                command.phoneNumber(),
                command.profileImageUrl()
        );

        // 3. Save changes
        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for user: {}", userId);

        // 4. Return DTO
        return userMapper.toDTO(updatedUser);
    }

    @Transactional
    public void deactivateUser(UUID userId) {
        log.info("Deactivating user: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (user.getStatus() == UserStatus.DEACTIVATED) {
            throw new BusinessException("User account is already deactivated", "ALREADY_DEACTIVATED");
        }
        user.deactivate();
        userRepository.save(user);

        log.info("User deactivated successfully: {}", userId);

        // TODO: In production, also:
        // - Blacklist all user's active tokens
        // - Send deactivation confirmation email
        // - Notify other services via Kafka event
    }


    @Transactional
    public void forgotPassword(ForgotPasswordCommand command) {
        log.info("Password reset requested for email: {}", command.email());

        // Find user (don't reveal if user doesn't exist for security)
        Optional<User> userOpt = userRepository.findByEmail(command.email());
        if (userOpt.isEmpty()) {
            log.warn("Password reset requested for non-existent email: {}", command.email());
            // Still return success to prevent email enumeration
            return;
        }

        User user = userOpt.get();

        // Delete any existing reset tokens for this user
        resetTokenRepository.deleteByUserId(user.getId());

        // Generate reset token (valid for 1 hour)
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.create(
                user.getId(),
                token,
                Instant.now().plusSeconds(3600)
        );
        resetTokenRepository.save(resetToken);

        // TODO: Send email with reset link
        String resetLink = "https://gigplatform.com/reset-password?token=" + token;
        log.info("Password reset link generated: {}", resetLink);
        // emailService.sendPasswordResetEmail(user.getEmail().getValue(), resetLink);
    }

    // ADD THIS METHOD
    @Transactional
    public void resetPassword(ResetPasswordCommand command) {
        log.info("Password reset attempt with token");

        // Find token
        PasswordResetToken resetToken = resetTokenRepository.findByToken(command.token())
                .orElseThrow(() -> new InvalidTokenException());

        // Validate token
        if (!resetToken.isValid()) {
            if (resetToken.isUsed()) {
                throw new BusinessException("Token has already been used", "TOKEN_ALREADY_USED");
            }
            throw new TokenExpiredException();
        }

        // Find user
        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException(resetToken.getUserId()));

        // Update password
        String hashedPassword = passwordEncoder.encode(command.newPassword());
        user.setPasswordHash(hashedPassword);
        userRepository.save(user);

        // Mark token as used
        resetToken.markAsUsed();
        resetTokenRepository.save(resetToken);

        log.info("Password reset successful for user: {}", user.getId());
    }


    @Transactional
    public void verifyEmail(String token) {
        log.info("Email verification attempt");

        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException());

        if (!verificationToken.isValid()) {
            throw new TokenExpiredException();
        }

        User user = userRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException(verificationToken.getUserId()));

        user.verifyEmail();
        userRepository.save(user);

        emailVerificationTokenRepository.delete(verificationToken);

        log.info("Email verified successfully for user: {}", user.getId());
    }


    @Transactional
    public void resendVerificationEmail(String email) {
        log.info("Resend verification email requested for: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (user.isEmailVerified()) {
            throw new BusinessException("Email already verified", "EMAIL_ALREADY_VERIFIED");
        }

        // Delete old token
        emailVerificationTokenRepository.deleteByUserId(user.getId());

        // Generate new token
        String verificationToken = UUID.randomUUID().toString();
        EmailVerificationToken token = EmailVerificationToken.create(
                user.getId(),
                verificationToken,
                Instant.now().plusSeconds(86400)
        );
        emailVerificationTokenRepository.save(token);

        // TODO: Send email
        String verifyLink = "https://gigplatform.com/verify-email?token=" + verificationToken;
        log.info("New verification link: {}", verifyLink);
    }
}