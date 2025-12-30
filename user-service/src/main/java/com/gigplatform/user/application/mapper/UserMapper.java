package com.gigplatform.user.application.mapper;

import com.gigplatform.user.application.dto.UserResponseDTO;
import com.gigplatform.user.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getEmail().getValue(),
                user.getUserType(),
                user.getStatus(),
                user.isEmailVerified(),
                user.getBio(),
                user.getPhoneNumber(),
                user.getProfileImageUrl(),
                user.getCreatedAt()
        );
    }
}