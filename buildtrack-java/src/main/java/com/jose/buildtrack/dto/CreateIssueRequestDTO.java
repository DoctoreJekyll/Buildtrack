package com.jose.buildtrack.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateIssueRequestDTO(

        @NotBlank(message = "Issue ID is required")
        String id,

        @NotBlank(message = "Issue title is required")
        String title,

        @NotBlank(message = "Issue severity is required")
        String severity
) {
}