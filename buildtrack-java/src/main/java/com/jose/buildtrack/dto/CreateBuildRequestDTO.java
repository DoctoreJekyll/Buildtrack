package com.jose.buildtrack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateBuildRequestDTO(

        @Schema(
                description = "Unique identifier of the build",
                example = "B-001"
        )
        @NotBlank(message = "Build ID is required")
        String id,

        @Schema(
                description = "Semantic version of the software build",
                example = "1.0.0"
        )
        @NotBlank(message = "Build version is required")
        String version,

        @Schema(
                description = "Target platform of the build",
                example = "WINDOWS"
        )
        @NotBlank(message = "Build platform is required")
        String platform

) {
}