package com.jose.buildtrack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateReleaseRequestDTO(

        @Schema(
                description = "Unique identifier of the release",
                example = "R-001"
        )
        @NotBlank(message = "Release ID is required")
        String id,

        @Schema(
                description = "Human-readable release name",
                example = "Release 1.0.0"
        )
        @NotBlank(message = "Release name is required")
        String name

) {
}