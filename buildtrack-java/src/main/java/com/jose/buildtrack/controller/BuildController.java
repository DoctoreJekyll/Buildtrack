package com.jose.buildtrack.controller;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.jose.buildtrack.domain.Build;
import com.jose.buildtrack.domain.Issue;
import com.jose.buildtrack.dto.BuildResponseDTO;
import com.jose.buildtrack.dto.CreateBuildRequestDTO;
import com.jose.buildtrack.dto.CreateIssueRequestDTO;
import com.jose.buildtrack.dto.ErrorResponseDTO;
import com.jose.buildtrack.dto.IssueResponseDTO;
import com.jose.buildtrack.dto.PageResponseDTO;
import com.jose.buildtrack.mapper.BuildMapper;
import com.jose.buildtrack.service.BuildService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/builds")
@Tag(
        name = "Builds",
        description = "Manage software builds, validation states and build issues"
)
public class BuildController {

    private final BuildService buildService;
    private final BuildMapper buildMapper;

    public BuildController(
            BuildService buildService,
            BuildMapper buildMapper
    ) {
        this.buildService = buildService;
        this.buildMapper = buildMapper;
    }

    @Operation(
            summary = "Create a build",
            description = "Creates a new software build in CREATED status."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Build created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = BuildResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or unsupported platform",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "A build with the same ID already exists",
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
    public ResponseEntity<BuildResponseDTO> createBuild(
            @Valid @RequestBody CreateBuildRequestDTO request
    ) {
        Build build = buildService.createBuild(
                request.id(),
                request.version(),
                buildMapper.toPlatform(request.platform())
        );

        BuildResponseDTO response =
                buildMapper.toBuildResponseDTO(build);

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
            summary = "Get a build by ID",
            description = "Returns the build identified by the provided ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Build found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = BuildResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Build not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public BuildResponseDTO getBuild(
            @Parameter(
                    description = "Unique identifier of the build",
                    example = "B-001"
            )
            @PathVariable @NonNull String id
    ) {
        Build build = buildService.findBuildById(id).get();

        return buildMapper.toBuildResponseDTO(build);
    }

    @Operation(
            summary = "Get builds",
            description = """
                    Returns a paginated collection of builds.

                    Results are ordered by build ID in ascending order.
                    Page numbering starts at zero.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Build page retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = PageResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination parameters",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            )
    })
    @GetMapping
    public PageResponseDTO<BuildResponseDTO> getAllBuilds(
            @Parameter(
                    description = "Zero-based page number",
                    example = "0"
            )
            @RequestParam(defaultValue = "0")
            @Min(
                    value = 0,
                    message = "Page must be greater than or equal to 0"
            )
            int page,

            @Parameter(
                    description = "Number of elements per page, between 1 and 100",
                    example = "10"
            )
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

        Page<BuildResponseDTO> buildPage = buildService
                .getAllBuilds(pageable)
                .map(buildMapper::toBuildResponseDTO);

        return new PageResponseDTO<>(
                buildPage.getContent(),
                buildPage.getNumber(),
                buildPage.getSize(),
                buildPage.getTotalElements(),
                buildPage.getTotalPages(),
                buildPage.isFirst(),
                buildPage.isLast()
        );
    }

    @Operation(
            summary = "Start build validation",
            description = "Moves a build from CREATED to VALIDATING."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Build moved to VALIDATING",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = BuildResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "The build is not in CREATED status",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Build not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            )
    })
    @PostMapping("/{id}/validate")
    public BuildResponseDTO validate(
            @Parameter(
                    description = "Unique identifier of the build",
                    example = "B-001"
            )
            @PathVariable @NonNull String id
    ) {
        Build build = buildService.startValidation(id);

        return buildMapper.toBuildResponseDTO(build);
    }

    @Operation(
            summary = "Approve a build",
            description = """
                    Moves a build from VALIDATING to APPROVED.

                    The build cannot be approved when it has open blocker issues.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Build approved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = BuildResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            The build is not in VALIDATING status or has
                            open blocker issues
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
                    description = "Build not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            )
    })
    @PostMapping("/{id}/approve")
    public BuildResponseDTO approve(
            @Parameter(
                    description = "Unique identifier of the build",
                    example = "B-001"
            )
            @PathVariable @NonNull String id
    ) {
        Build build = buildService.approveBuild(id);

        return buildMapper.toBuildResponseDTO(build);
    }

    @Operation(
            summary = "Reject a build",
            description = "Moves a build from VALIDATING to REJECTED."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Build rejected successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = BuildResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "The build is not in VALIDATING status",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Build not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            )
    })
    @PostMapping("/{id}/reject")
    public BuildResponseDTO reject(
            @Parameter(
                    description = "Unique identifier of the build",
                    example = "B-001"
            )
            @PathVariable @NonNull String id
    ) {
        Build build = buildService.rejectBuild(id);

        return buildMapper.toBuildResponseDTO(build);
    }

    @Operation(
            summary = "Add an issue to a build",
            description = "Creates an issue and associates it with the selected build."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Issue added successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = BuildResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Invalid request data, unsupported severity or
                            duplicated issue ID
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
                    description = "Build not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            )
    })
    @PostMapping("/{id}/issues")
    public BuildResponseDTO addIssue(
            @Parameter(
                    description = "Unique identifier of the build",
                    example = "B-001"
            )
            @PathVariable @NonNull String id,

            @Valid @RequestBody CreateIssueRequestDTO request
    ) {
        Build build = buildService.addIssueToBuild(
                id,
                request.id(),
                request.title(),
                buildMapper.toIssueSeverity(request.severity())
        );

        return buildMapper.toBuildResponseDTO(build);
    }

    @Operation(
            summary = "Resolve a build issue",
            description = "Moves the selected issue from OPEN to RESOLVED."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Issue resolved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = BuildResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "The issue is not in OPEN status",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Build or issue not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            )
    })
    @PostMapping("/{buildId}/issues/{issueId}/resolve")
    public BuildResponseDTO resolveIssue(
            @Parameter(
                    description = "Unique identifier of the build",
                    example = "B-001"
            )
            @PathVariable @NonNull String buildId,

            @Parameter(
                    description = "Unique identifier of the issue",
                    example = "ISSUE-001"
            )
            @PathVariable @NonNull String issueId
    ) {
        Build build = buildService.resolveIssue(buildId, issueId);

        return buildMapper.toBuildResponseDTO(build);
    }

    @Operation(
            summary = "Get issues from a build",
            description = "Returns every issue associated with the selected build."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Issues retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = IssueResponseDTO.class
                                    )
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Build not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            )
    })
    @GetMapping("/{buildId}/issues")
    public List<IssueResponseDTO> getIssuesByBuild(
            @Parameter(
                    description = "Unique identifier of the build",
                    example = "B-001"
            )
            @PathVariable @NonNull String buildId
    ) {
        return buildService.getIssuesByBuildId(buildId)
                .stream()
                .map(buildMapper::toIssueResponseDTO)
                .toList();
    }

    @Operation(
            summary = "Get a build issue by ID",
            description = "Returns a specific issue associated with the selected build."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Issue found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = IssueResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Build or issue not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            )
                    )
            )
    })
    @GetMapping("/{buildId}/issues/{issueId}")
    public IssueResponseDTO getIssueById(
            @Parameter(
                    description = "Unique identifier of the build",
                    example = "B-001"
            )
            @PathVariable @NonNull String buildId,

            @Parameter(
                    description = "Unique identifier of the issue",
                    example = "ISSUE-001"
            )
            @PathVariable @NonNull String issueId
    ) {
        Issue issue = buildService.getIssueById(buildId, issueId);

        return buildMapper.toIssueResponseDTO(issue);
    }
}