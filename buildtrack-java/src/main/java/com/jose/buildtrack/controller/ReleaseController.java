package com.jose.buildtrack.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.jose.buildtrack.domain.Release;
import com.jose.buildtrack.dto.CreateReleaseRequestDTO;
import com.jose.buildtrack.dto.ReleaseResponseDTO;
import com.jose.buildtrack.mapper.ReleaseMapper;
import com.jose.buildtrack.service.ReleaseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

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

    @SuppressWarnings("null")
    @Operation(
            summary = "Create a release",
            description = "Creates a new release in DRAFT status."
    )
    @PostMapping
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

    @Operation(
            summary = "Add a build to a release",
            description = "Associates an existing build with a DRAFT release."
    )
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

    @Operation(
            summary = "Prepare a release",
            description = """
                    Moves a release from DRAFT to READY when it contains builds,
                    all builds are approved and no open blocker issues exist.
                    """
    )
    @PostMapping("/{releaseId}/prepare")
    public ReleaseResponseDTO prepareRelease(
            @PathVariable @NonNull String releaseId
    ) {
        Release release = releaseService.prepareRelease(releaseId);

        return releaseMapper.toReleaseResponseDTO(release);
    }

    @Operation(
            summary = "Publish a release",
            description = "Moves a release from READY to PUBLISHED."
    )
    @PostMapping("/{releaseId}/publish")
    public ReleaseResponseDTO publishRelease(
            @PathVariable @NonNull String releaseId
    ) {
        Release release = releaseService.publishRelease(releaseId);

        return releaseMapper.toReleaseResponseDTO(release);
    }

    @Operation(
            summary = "Get all releases",
            description = "Returns every release currently stored in the system."
    )
    @GetMapping
    public List<ReleaseResponseDTO> getAllReleases() {
        return releaseService.getAllReleases()
                .stream()
                .map(releaseMapper::toReleaseResponseDTO)
                .toList();
    }

    @Operation(
            summary = "Get a release by ID",
            description = "Returns the release identified by the provided ID."
    )
    @GetMapping("/{id}")
    public ReleaseResponseDTO getRelease(
            @PathVariable @NonNull String id
    ) {
        Release release = releaseService.getReleaseById(id);

        return releaseMapper.toReleaseResponseDTO(release);
    }

    @Operation(
            summary = "Delete a release",
            description = "Deletes the release identified by the provided ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRelease(
            @PathVariable @NonNull String id
    ) {
        releaseService.deleteRelease(id);

        /*
         * La eliminación ha terminado correctamente,
         * pero no devolvemos ningún contenido.
         */
        return ResponseEntity.noContent().build();
    }
}