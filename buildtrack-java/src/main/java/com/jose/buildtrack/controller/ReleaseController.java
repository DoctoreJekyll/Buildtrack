package com.jose.buildtrack.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.jose.buildtrack.domain.Release;
import com.jose.buildtrack.domain.ReleaseStatus;
import com.jose.buildtrack.dto.CreateReleaseRequestDTO;
import com.jose.buildtrack.dto.PageResponseDTO;
import com.jose.buildtrack.dto.ReleaseResponseDTO;
import com.jose.buildtrack.mapper.ReleaseMapper;
import com.jose.buildtrack.service.ReleaseService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/releases")
@Tag(
        name = "Releases",
        description = "Manage release preparation, build association and publication"
)
public class ReleaseController {

    private final ReleaseService releaseService;
    private final ReleaseMapper releaseMapper;

    public ReleaseController(
            ReleaseService releaseService,
            ReleaseMapper releaseMapper
    ) {
        this.releaseService = releaseService;
        this.releaseMapper = releaseMapper;
    }

    @PostMapping
    @SuppressWarnings("null")
    public ResponseEntity<ReleaseResponseDTO> createRelease(
            @Valid @RequestBody CreateReleaseRequestDTO request
    ) {
        Release release = releaseService.createRelease(
                request.id(),
                request.name()
        );

        ReleaseResponseDTO response =
                releaseMapper.toReleaseResponseDTO(release);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(request.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @PostMapping("/{releaseId}/builds/{buildId}")
    public ReleaseResponseDTO addBuildToRelease(
            @PathVariable @NonNull String releaseId,
            @PathVariable @NonNull String buildId
    ) {
        Release release = releaseService.addBuildToRelease(
                releaseId,
                buildId
        );

        return releaseMapper.toReleaseResponseDTO(release);
    }

    @PostMapping("/{releaseId}/prepare")
    public ReleaseResponseDTO prepareRelease(
            @PathVariable @NonNull String releaseId
    ) {
        Release release =
                releaseService.prepareRelease(releaseId);

        return releaseMapper.toReleaseResponseDTO(release);
    }

    @PostMapping("/{releaseId}/publish")
    public ReleaseResponseDTO publishRelease(
            @PathVariable @NonNull String releaseId
    ) {
        Release release =
                releaseService.publishRelease(releaseId);

        return releaseMapper.toReleaseResponseDTO(release);
    }

    @GetMapping
    public PageResponseDTO<ReleaseResponseDTO> getReleases(

            @RequestParam(required = false)
            ReleaseStatus status,

            @RequestParam(defaultValue = "0")
            @Min(
                    value = 0,
                    message = "Page must be greater than or equal to 0"
            )
            int page,

            @RequestParam(defaultValue = "10")
            @Min(
                    value = 1,
                    message = "Page size must be greater than or equal to 1"
            )
            @Max(
                    value = 100,
                    message = "Page size must be less than or equal to 100"
            )
            int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "id")
        );

        Page<ReleaseResponseDTO> releasePage = releaseService
                .searchReleases(status, pageable)
                .map(releaseMapper::toReleaseResponseDTO);

        return new PageResponseDTO<>(
                releasePage.getContent(),
                releasePage.getNumber(),
                releasePage.getSize(),
                releasePage.getTotalElements(),
                releasePage.getTotalPages(),
                releasePage.isFirst(),
                releasePage.isLast()
        );
    }

    @GetMapping("/{id}")
    public ReleaseResponseDTO getRelease(
            @PathVariable @NonNull String id
    ) {
        Release release =
                releaseService.getReleaseById(id);

        return releaseMapper.toReleaseResponseDTO(release);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRelease(
            @PathVariable @NonNull String id
    ) {
        releaseService.deleteRelease(id);

        return ResponseEntity.noContent().build();
    }
}