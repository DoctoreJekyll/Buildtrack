package com.jose.buildtrack.dto;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.constraints.NotBlank;

public record CreateBuildRequestDTO(
    @NotBlank(message = "Build ID is required") @NonNull
    String id, 
    @NotBlank(message = "version cannot be blank")
    String version, 
    @NotBlank(message = "platform cannot be blank")
    String platform
) {

}
