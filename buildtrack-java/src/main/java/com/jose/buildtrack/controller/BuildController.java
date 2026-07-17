package com.jose.buildtrack.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.jose.buildtrack.domain.Build;
import com.jose.buildtrack.domain.Issue;
import com.jose.buildtrack.dto.BuildResponseDTO;
import com.jose.buildtrack.dto.CreateBuildRequestDTO;
import com.jose.buildtrack.dto.CreateIssueRequestDTO;
import com.jose.buildtrack.dto.IssueResponseDTO;
import com.jose.buildtrack.mapper.BuildMapper;
import com.jose.buildtrack.service.BuildService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

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
    @PostMapping
    public ResponseEntity<BuildResponseDTO> createBuild(
            @Valid @RequestBody CreateBuildRequestDTO request
    ) {
        @SuppressWarnings("null")
        Build build = buildService.createBuild(
                request.id(),
                request.version(),
                buildMapper.toPlatform(request.platform())
        );

        BuildResponseDTO response = buildMapper.toBuildResponseDTO(build);

        /*
         * Construye la URL del recurso recién creado.
         *
         * Si la petición ha sido:
         * POST /builds
         *
         * y el ID es B-001, la cabecera será:
         * Location: /builds/B-001
         */
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(request.id())
                .toUri();

        /*
         * ResponseEntity.created(location):
         *
         * - devuelve HTTP 201 Created
         * - añade la cabecera Location
         * - incluye el DTO en el cuerpo
         */
        return ResponseEntity
                .created(location)
                .body(response);
    }

    @Operation(
            summary = "Get a build by ID",
            description = "Returns the build identified by the provided ID."
    )
    @GetMapping("/{id}")
    public BuildResponseDTO getBuild(@PathVariable @NonNull String id) {
        Build build = buildService.findBuildById(id).get();

        return buildMapper.toBuildResponseDTO(build);
    }

    @Operation(
            summary = "Get all builds",
            description = "Returns every build currently stored in the system."
    )
    @GetMapping
    public List<BuildResponseDTO> getAllBuilds() {
        return buildService.getAllBuilds()
                .stream()
                .map(buildMapper::toBuildResponseDTO)
                .toList();
    }

    @Operation(
            summary = "Start build validation",
            description = "Moves a build from CREATED to VALIDATING."
    )
    @PostMapping("/{id}/validate")
    public BuildResponseDTO validate(@PathVariable @NonNull String id) {
        Build build = buildService.startValidation(id);

        return buildMapper.toBuildResponseDTO(build);
    }

    @Operation(
            summary = "Approve a build",
            description = """
                    Moves a build from VALIDATING to APPROVED when it has
                    no open blocker issues.
                    """
    )
    @PostMapping("/{id}/approve")
    public BuildResponseDTO approve(@PathVariable @NonNull String id) {
        Build build = buildService.approveBuild(id);

        return buildMapper.toBuildResponseDTO(build);
    }

    @Operation(
            summary = "Reject a build",
            description = "Moves a build from VALIDATING to REJECTED."
    )
    @PostMapping("/{id}/reject")
    public BuildResponseDTO reject(@PathVariable @NonNull String id) {
        Build build = buildService.rejectBuild(id);

        return buildMapper.toBuildResponseDTO(build);
    }

    @Operation(
            summary = "Add an issue to a build",
            description = "Creates an issue and associates it with the selected build."
    )
    @PostMapping("/{id}/issues")
    public BuildResponseDTO addIssue(
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
    @PostMapping("/{buildId}/issues/{issueId}/resolve")
    public BuildResponseDTO resolveIssue(
            @PathVariable @NonNull String buildId,
            @PathVariable String issueId
    ) {
        Build build = buildService.resolveIssue(buildId, issueId);

        return buildMapper.toBuildResponseDTO(build);
    }

    @Operation(
            summary = "Get issues from a build",
            description = "Returns every issue associated with the selected build."
    )
    @GetMapping("/{buildId}/issues")
    public List<IssueResponseDTO> getIssuesByBuild(
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
    @GetMapping("/{buildId}/issues/{issueId}")
    public IssueResponseDTO getIssueById(
            @PathVariable @NonNull String buildId,
            @PathVariable String issueId
    ) {
        Issue issue = buildService.getIssueById(buildId, issueId);

        return buildMapper.toIssueResponseDTO(issue);
    }
}