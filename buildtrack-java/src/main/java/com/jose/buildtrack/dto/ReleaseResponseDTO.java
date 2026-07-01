package com.jose.buildtrack.dto;

import java.util.List;

public record ReleaseResponseDTO(
    String id,
    String name,
    String status,
    List<BuildResponseDTO> builds
) {

}
