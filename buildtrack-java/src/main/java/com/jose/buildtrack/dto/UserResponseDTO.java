package com.jose.buildtrack.dto;

import java.time.Instant;

import com.jose.buildtrack.domain.UserRole;

public record UserResponseDTO(
        Long id,
        String username,
        UserRole role,
        boolean enabled,
        Instant createdAt
) {
}