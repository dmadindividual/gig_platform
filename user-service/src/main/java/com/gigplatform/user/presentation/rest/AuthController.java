package com.gigplatform.user.presentation.rest;

import com.gigplatform.shared.dto.ApiResponse;
import com.gigplatform.user.application.command.*;
import com.gigplatform.user.application.dto.AuthResponseDTO;
import com.gigplatform.user.application.service.UserApplicationService;
import com.gigplatform.user.domain.exception.InvalidCredentialsException;
import com.gigplatform.user.domain.exception.IpBlockedException;
import com.gigplatform.user.domain.exception.RateLimitExceededException;
import com.gigplatform.user.infrastructure.security.IpBlacklistService;
import com.gigplatform.user.infrastructure.security.RateLimiterService;
import com.gigplatform.user.infrastructure.security.TokenBlacklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "User authentication endpoints")

public class AuthController {

    private final UserApplicationService userApplicationService;
    private final TokenBlacklistService tokenBlacklistService;  // ADD THIS
    private final RateLimiterService rateLimiterService;  // ADD THIS
    private final IpBlacklistService ipBlacklistService;  // ADD THIS
    private final RedisTemplate<String, String> redisTemplate;  // ADD THIS





    @Operation(summary = "Register new user", description = "Creates a new user account")  // ADD
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email already exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> register(
            @Valid @RequestBody RegisterUserCommand command,
            HttpServletRequest request

    ) {
        log.info("Registration request received for email: {}", command.email());
        String ipAddress = getClientIp(request);
        String rateLimitKey = "register:" + ipAddress;
        if (!rateLimiterService.isAllowed(rateLimitKey, 3, Duration.ofHours(1))) {
            log.warn("Registration rate limit exceeded for IP: {}", ipAddress);
            throw new RateLimitExceededException("Too many registration attempts. Please try again later.");
        }


        AuthResponseDTO response = userApplicationService.registerUser(command);

        log.info("User registered successfully with ID: {}", response.user().id());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "User registered successfully"));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Auth service is healthy"));
    }


    @Operation(summary = "Login", description = "Authenticate user and return JWT tokens")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(
            @Valid @RequestBody LoginCommand command,
            HttpServletRequest request

    ) {
        log.info("Login request received for email: {}", command.email());
        String ipAddress = getClientIp(request);
        String rateLimitKey = "login:" + ipAddress;
        String failureKey = "login_failures:" + ipAddress;  // ADD

        if (!rateLimiterService.isAllowed(rateLimitKey, 5, Duration.ofMinutes(1))) {
            log.warn("Login rate limit exceeded for IP: {}", ipAddress);
            throw new RateLimitExceededException("Too many login attempts. Please try again in 1 minute.");
        }


        try {
            AuthResponseDTO response = userApplicationService.login(command);
            rateLimiterService.reset(rateLimitKey);
            redisTemplate.delete(failureKey);  // ADD: Reset failure count



            return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));

        } catch (InvalidCredentialsException e) {
            // ADD: Track failed attempts
            Long failures = redisTemplate.opsForValue().increment(failureKey);

            if (failures == 1) {
                // Set expiry on first failure
                redisTemplate.expire(failureKey, Duration.ofMinutes(15));
            }

            log.warn("Failed login attempt {} for IP: {}", failures, ipAddress);

            // ADD: Auto-block after 10 failed attempts
            if (failures >= 10) {
                ipBlacklistService.blockIp(ipAddress, Duration.ofHours(24));
                log.error("IP blocked due to excessive failed login attempts: {}", ipAddress);
                throw new IpBlockedException("Your IP has been blocked due to excessive failed login attempts. Please contact support.");
            }

            throw e;
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> refreshToken(
            @Valid @RequestBody RefreshTokenCommand command
    ) {
        log.info("Token refresh request received");

        AuthResponseDTO response = userApplicationService.refreshToken(command);

        log.info("Tokens refreshed successfully for user: {}", response.user().id());

        return ResponseEntity.ok(ApiResponse.success(response, "Tokens refreshed successfully"));
    }


    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request
    ) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            Duration ttl = Duration.ofSeconds(86400); // 24 hours

            tokenBlacklistService.blacklistToken(token, ttl);
            log.info("User logged out successfully");
        }

        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordCommand command
    ) {
        log.info("Password reset requested for email: {}", command.email());

        userApplicationService.forgotPassword(command);

        return ResponseEntity.ok(ApiResponse.success(
                null,
                "If the email exists, a password reset link has been sent"
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordCommand command
    ) {
        log.info("Password reset attempt");

        userApplicationService.resetPassword(command);

        return ResponseEntity.ok(ApiResponse.success(
                null,
                "Password has been reset successfully"
        ));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String token) {
        log.info("Email verification request received");

        userApplicationService.verifyEmail(token);

        return ResponseEntity.ok(ApiResponse.success(
                null,
                "Email verified successfully"
        ));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Void>> resendVerification(
            @RequestParam String email
    ) {
        log.info("Resend verification request for: {}", email);

        userApplicationService.resendVerificationEmail(email);

        return ResponseEntity.ok(ApiResponse.success(
                null,
                "Verification email sent"
        ));
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip != null ? ip : "unknown";
    }
}