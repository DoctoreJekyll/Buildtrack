package com.jose.buildtrack.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.jose.buildtrack.domain.Release;
import com.jose.buildtrack.dto.CreateReleaseRequestDTO;
import com.jose.buildtrack.dto.ReleaseResponseDTO;
import com.jose.buildtrack.mapper.ReleaseMapper;
import com.jose.buildtrack.service.ReleaseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/releases")
public class ReleaseController {

    private final ReleaseService releaseService;
    private final ReleaseMapper releaseMapper;

    public ReleaseController(ReleaseService releaseService, ReleaseMapper releaseMapper) {
        this.releaseService = releaseService;
        this.releaseMapper = releaseMapper;
    }

    @PostMapping
    public ReleaseResponseDTO create(@Valid @RequestBody CreateReleaseRequestDTO request) {

        Release release = releaseService.createRelease(
                request.id(),
                request.name()
        );

        return releaseMapper.toReleaseResponseDTO(release);
    }

    @GetMapping
    public List<ReleaseResponseDTO> getAllReleases() {

        return releaseService.getAllReleases()
                .stream()
                .map(releaseMapper::toReleaseResponseDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ReleaseResponseDTO getRelease(@PathVariable String id) {

        Release release = releaseService.getReleaseById(id);

        return releaseMapper.toReleaseResponseDTO(release);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRelease(@PathVariable String id) {
        releaseService.deleteRelease(id);
    }
}