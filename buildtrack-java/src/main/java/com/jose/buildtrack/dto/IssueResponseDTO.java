package com.jose.buildtrack.dto;

public record IssueResponseDTO(
        String id,
        String title,
        String severity,
        String status
) {
}