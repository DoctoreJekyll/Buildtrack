package com.jose.buildtrack.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Paginated API response")
public record PageResponseDTO<T>(

        @Schema(description = "Elements contained in the current page")
        List<T> content,

        @Schema(description = "Current zero-based page number", example = "0")
        int page,

        @Schema(description = "Maximum number of elements per page", example = "10")
        int size,

        @Schema(description = "Total number of matching elements", example = "25")
        long totalElements,

        @Schema(description = "Total number of available pages", example = "3")
        int totalPages,

        @Schema(description = "Indicates whether this is the first page", example = "true")
        boolean first,

        @Schema(description = "Indicates whether this is the last page", example = "false")
        boolean last

) {
}