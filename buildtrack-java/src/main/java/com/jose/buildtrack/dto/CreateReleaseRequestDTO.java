package com.jose.buildtrack.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateReleaseRequestDTO(
    @NotBlank(message = "Build ID is required")
    String id,
    
    @NotBlank
    String name
) {

}
