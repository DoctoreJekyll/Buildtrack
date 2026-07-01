package com.jose.buildtrack.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateReleaseRequestDTO(
    @NotBlank
    String id,
    
    @NotBlank
    String name
) {

}
