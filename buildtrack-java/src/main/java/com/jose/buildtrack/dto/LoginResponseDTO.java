package com.jose.buildtrack.dto;

import com.jose.buildtrack.domain.UserRole;

public record LoginResponseDTO(
        String accessToken,
        String tokenType,
        long expiresIn,
        String username,
        UserRole role
) {
}