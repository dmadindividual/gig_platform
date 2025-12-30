package com.gigplatform.user.application.dto;

public record AuthResponseDTO(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserResponseDTO user
) {
    public AuthResponseDTO(String accessToken, String refreshToken, long expiresIn, UserResponseDTO user) {
        this(accessToken, refreshToken, "Bearer", expiresIn, user);
    }
}