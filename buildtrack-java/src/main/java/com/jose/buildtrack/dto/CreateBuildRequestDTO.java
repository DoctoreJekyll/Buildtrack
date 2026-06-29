package com.jose.buildtrack.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateBuildRequestDTO(
    @NotBlank(message = "id cannot be blank")
    String id, 
    @NotBlank(message = "version cannot be blank")
    String version, 
    @NotBlank(message = "platform cannot be blank")
    String platform
) {

}
