package com.jose.buildtrack.dto;

import java.time.Instant;
import java.util.List;

public record ReleaseResponseDTO(
    String id,
    String name,
    String status,
    List<BuildResponseDTO> builds,
    Instant createdAt,
    Instant updatedAt,
    Instant publishedAt
) {

}
