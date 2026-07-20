package com.jose.buildtrack.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponseDTO(

        @Schema(
                description = "HTTP status code",
                example = "404"
        )
        int status,

        @Schema(
                description = "HTTP error category",
                example = "Not Found"
        )
        String error,

        @Schema(
                description = "Detailed error message",
                example = "Build with ID B-001 not found"
        )
        String message

) {
}