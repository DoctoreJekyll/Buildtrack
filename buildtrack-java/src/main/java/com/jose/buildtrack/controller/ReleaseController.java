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
import com.jose.buildtrack.dto.ErrorResponseDTO;
import com.jose.buildtrack.dto.ReleaseResponseDTO;
import com.jose.buildtrack.mapper.ReleaseMapper;
import com.jose.buildtrack.service.ReleaseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "Create a release",
            description = "Creates a new release in DRAFT status."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Release created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ReleaseResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid release data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "A release with the same ID already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            )
    })
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

    @Operation(
            summary = "Add a build to a release",
            description = "Associates an existing build with a DRAFT release."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Build added to the release successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ReleaseResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            The release is not in DRAFT status or the build
                            is already associated with the release
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Release or build not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            )
    })
    @PostMapping("/{releaseId}/builds/{buildId}")
    public ReleaseResponseDTO addBuildToRelease(
            @Parameter(
                    description = "Unique identifier of the release",
                    example = "R-001"
            )
            @PathVariable @NonNull String releaseId,

            @Parameter(
                    description = "Unique identifier of the build",
                    example = "B-001"
            )
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
                    Moves a release from DRAFT to READY.

                    The release must contain at least one build, all builds
                    must be approved and no open blocker issues may exist.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Release moved to READY",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ReleaseResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "The release does not satisfy the preparation rules",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Release not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            )
    })
    @PostMapping("/{releaseId}/prepare")
    public ReleaseResponseDTO prepareRelease(
            @Parameter(
                    description = "Unique identifier of the release",
                    example = "R-001"
            )
            @PathVariable @NonNull String releaseId
    ) {
        Release release = releaseService.prepareRelease(releaseId);

        return releaseMapper.toReleaseResponseDTO(release);
    }

    @Operation(
            summary = "Publish a release",
            description = "Moves a release from READY to PUBLISHED."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Release published successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ReleaseResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "The release is not in READY status",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Release not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            )
    })
    @PostMapping("/{releaseId}/publish")
    public ReleaseResponseDTO publishRelease(
            @Parameter(
                    description = "Unique identifier of the release",
                    example = "R-001"
            )
            @PathVariable @NonNull String releaseId
    ) {
        Release release = releaseService.publishRelease(releaseId);

        return releaseMapper.toReleaseResponseDTO(release);
    }

    @Operation(
            summary = "Get all releases",
            description = "Returns every release currently stored in the system."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Releases retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(
                                    implementation = ReleaseResponseDTO.class
                            )
                    )
            )
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
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Release found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ReleaseResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Release not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ReleaseResponseDTO getRelease(
            @Parameter(
                    description = "Unique identifier of the release",
                    example = "R-001"
            )
            @PathVariable @NonNull String id
    ) {
        Release release = releaseService.getReleaseById(id);

        return releaseMapper.toReleaseResponseDTO(release);
    }

    @Operation(
            summary = "Delete a release",
            description = "Deletes the release identified by the provided ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Release deleted successfully",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Release not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRelease(
            @Parameter(
                    description = "Unique identifier of the release",
                    example = "R-001"
            )
            @PathVariable @NonNull String id
    ) {
        releaseService.deleteRelease(id);

        return ResponseEntity.noContent().build();
    }
}