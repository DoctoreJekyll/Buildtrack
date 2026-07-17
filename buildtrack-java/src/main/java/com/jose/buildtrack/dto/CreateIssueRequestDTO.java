package com.jose.buildtrack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateIssueRequestDTO(

        @Schema(
                description = "Unique identifier of the issue within the build",
                example = "ISSUE-001"
        )
        @NotBlank(message = "Issue ID is required")
        String id,

        @Schema(
                description = "Short description of the validation issue",
                example = "Application crashes during startup"
        )
        @NotBlank(message = "Issue title is required")
        String title,

        @Schema(
                description = "Severity assigned to the issue",
                example = "BLOCKER"
        )
        @NotBlank(message = "Issue severity is required")
        String severity

) {
}