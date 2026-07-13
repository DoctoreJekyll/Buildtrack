package com.jose.buildtrack.dto;

import java.util.List;

public record BuildResponseDTO(
    Integer id, 
    String version, 
    String platform, 
    String status,
    List<IssueResponseDTO> issues) {

}
