package com.jose.buildtrack.dto;

import java.time.Instant;
import java.util.List;

public record BuildResponseDTO(
    String id, 
    String version, 
    String platform, 
    String status,
    List<IssueResponseDTO> issues,
    Instant createdAt,
    Instant updatedAt,
    Instant completedAt
) {

}
