package com.jose.buildtrack.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jose.buildtrack.domain.Build;
import com.jose.buildtrack.domain.Platform;
import com.jose.buildtrack.dto.BuildResponseDTO;
import com.jose.buildtrack.dto.CreateBuildRequestDTO;
import com.jose.buildtrack.exceptions.BuildNotFoundException;
import com.jose.buildtrack.service.BuildService;

@RestController
@RequestMapping("/builds")
public class BuildController {
    private final BuildService buildService;

    public BuildController(BuildService buildService) {
        this.buildService = buildService;
    }

    @PostMapping
    public BuildResponseDTO create(@RequestBody CreateBuildRequestDTO request)
    {
        Build build = buildService.createBuild(
            request.id(), 
            request.version(), 
            Platform.valueOf(request.platform().toUpperCase()));

        return toResponseDTO(build);
    }

    @GetMapping("/{id}")
    public BuildResponseDTO getBuild(@PathVariable String id) {
        Build build = buildService.findBuildById(id)
                .orElseThrow(() -> new BuildNotFoundException(id));
        
        return toResponseDTO(build);
    }

    @GetMapping
    public List<BuildResponseDTO> getAllBuilds() {
        return buildService.getAllBuilds()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @PostMapping("/{id}/validate")
    public BuildResponseDTO validate(@PathVariable String id) {
        Build build = buildService.startValidation(id);
        return toResponseDTO(build);
    }

    @PostMapping("/{id}/approve")
    public BuildResponseDTO approve(@PathVariable String id) {
        Build build = buildService.approveBuild(id);
        return toResponseDTO(build);
    }

    @PostMapping("/{id}/reject")
    public BuildResponseDTO reject(@PathVariable String id) {
        Build build = buildService.rejectBuild(id);
        return toResponseDTO(build);
    }

    private BuildResponseDTO toResponseDTO(Build build) {
        return new BuildResponseDTO(
            build.getId(),
            build.getVersion().getValue(),
            build.getPlatform().name(),
            build.getStatus().name()
        );
    }

}
