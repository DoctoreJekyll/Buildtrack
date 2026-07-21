package com.jose.buildtrack.mapper;

import org.springframework.stereotype.Component;

import com.jose.buildtrack.domain.AppUser;
import com.jose.buildtrack.dto.UserResponseDTO;

@Component
public class AppUserMapper {

    public UserResponseDTO toUserResponseDTO(AppUser user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.isEnabled(),
                user.getCreatedAt()
        );
    }
}