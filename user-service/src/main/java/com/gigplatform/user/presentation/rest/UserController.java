package com.gigplatform.user.presentation.rest;

import com.gigplatform.shared.dto.ApiResponse;
import com.gigplatform.user.application.command.UpdateUserProfileCommand;
import com.gigplatform.user.application.dto.UserResponseDTO;
import com.gigplatform.user.application.mapper.UserMapper;
import com.gigplatform.user.application.service.UserApplicationService;
import com.gigplatform.user.domain.exception.UserNotFoundException;
import com.gigplatform.user.domain.model.User;
import com.gigplatform.user.domain.repository.UserRepository;
import com.gigplatform.user.infrastructure.security.JwtAuthenticationToken;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserApplicationService userApplicationService;  // ADD THIS


    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getCurrentUser(
            @AuthenticationPrincipal JwtAuthenticationToken authentication
    ) {
        UUID userId = authentication.getUserId();
        log.info("Fetching profile for current user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        UserResponseDTO userDTO = userMapper.toDTO(user);

        return ResponseEntity.ok(ApiResponse.success(userDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(
            @PathVariable UUID id,
            @AuthenticationPrincipal JwtAuthenticationToken authentication
    ) {
        log.info("User {} requesting profile of user {}", authentication.getUserId(), id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        UserResponseDTO userDTO = userMapper.toDTO(user);

        return ResponseEntity.ok(ApiResponse.success(userDTO));
    }

    @GetMapping("/workers-only")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<ApiResponse<String>> workerOnlyEndpoint() {
        log.info("Worker-only endpoint accessed");
        return ResponseEntity.ok(ApiResponse.success("This is a worker-only endpoint"));
    }

    @GetMapping("/clients-only")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ApiResponse<String>> clientOnlyEndpoint() {
        log.info("Client-only endpoint accessed");
        return ResponseEntity.ok(ApiResponse.success("This is a client-only endpoint"));
    }


    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateCurrentUserProfile(
            @Valid @RequestBody UpdateUserProfileCommand command,
            @AuthenticationPrincipal JwtAuthenticationToken authentication
    ) {
        UUID userId = authentication.getUserId();
        log.info("User {} updating their profile", userId);

        UserResponseDTO updatedUser = userApplicationService.updateUserProfile(userId, command);

        return ResponseEntity.ok(ApiResponse.success(updatedUser, "Profile updated successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")  // Only admins can update other users
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUserProfile(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserProfileCommand command
    ) {
        log.info("Admin updating profile for user: {}", id);

        UserResponseDTO updatedUser = userApplicationService.updateUserProfile(id, command);

        return ResponseEntity.ok(ApiResponse.success(updatedUser, "Profile updated successfully"));
    }

    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deactivateCurrentUser(
            @AuthenticationPrincipal JwtAuthenticationToken authentication
    ) {
        UUID userId = authentication.getUserId();
        log.info("User {} requesting account deactivation", userId);

        userApplicationService.deactivateUser(userId);

        return ResponseEntity.ok(ApiResponse.success(null, "Account deactivated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable UUID id) {
        log.info("Admin deactivating user: {}", id);

        userApplicationService.deactivateUser(id);

        return ResponseEntity.ok(ApiResponse.success(null, "User deactivated successfully"));
    }

}