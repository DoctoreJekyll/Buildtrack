package com.jose.buildtrack.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jose.buildtrack.domain.Build;
import com.jose.buildtrack.domain.Platform;
import com.jose.buildtrack.service.BuildService;

@RestController
@RequestMapping("/builds")
public class BuildController {
    private final BuildService buildService;

    public BuildController(BuildService buildService) {
        this.buildService = buildService;
    }

    @PostMapping
    public Build create(
            @RequestParam String id,
            @RequestParam String version,
            @RequestParam Platform platform
    ) {
        return buildService.createBuild(id, version, platform);
    }

    @GetMapping("/{id}")
    public Build getBuild(@PathVariable String id) {
        return buildService.findBuildById(id)
                .orElseThrow(() -> new IllegalArgumentException("Build not found"));
    }

    @PostMapping("/{id}/validate")
    public void validate(@PathVariable String id) {
        buildService.startValidation(id);
    }

}
