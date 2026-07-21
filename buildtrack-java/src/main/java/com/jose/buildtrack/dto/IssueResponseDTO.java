package com.jose.buildtrack.dto;

import java.time.Instant;

public record IssueResponseDTO(
        String id,
        String title,
        String severity,
        String status,
        Instant createdAt,
        Instant updatedAt,
        Instant resolvedAt
) {
}