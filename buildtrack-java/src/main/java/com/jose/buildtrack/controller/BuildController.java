package com.jose.buildtrack.controller;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import com.jose.buildtrack.domain.BuildStatus;
import com.jose.buildtrack.domain.Issue;
import com.jose.buildtrack.domain.Platform;
import com.jose.buildtrack.dto.BuildResponseDTO;
import com.jose.buildtrack.dto.CreateBuildRequestDTO;
import com.jose.buildtrack.dto.CreateIssueRequestDTO;
import com.jose.buildtrack.dto.IssueResponseDTO;
import com.jose.buildtrack.dto.PageResponseDTO;
import com.jose.buildtrack.mapper.BuildMapper;
import com.jose.buildtrack.service.BuildService;

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

    @GetMapping("/{id}")
    public BuildResponseDTO getBuild(
            @PathVariable @NonNull String id
    ) {
        Build build = buildService.getBuildById(id);

        return buildMapper.toBuildResponseDTO(build);
    }

    @GetMapping
    public PageResponseDTO<BuildResponseDTO> getBuilds(

            @RequestParam(required = false)
            BuildStatus status,

            @RequestParam(required = false)
            Platform platform,

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

        Page<BuildResponseDTO> buildPage = buildService
                .searchBuilds(status, platform, pageable)
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

    @PostMapping("/{id}/validate")
    public BuildResponseDTO validate(
            @PathVariable @NonNull String id
    ) {
        Build build = buildService.startValidation(id);

        return buildMapper.toBuildResponseDTO(build);
    }

    @PostMapping("/{id}/approve")
    public BuildResponseDTO approve(
            @PathVariable @NonNull String id
    ) {
        Build build = buildService.approveBuild(id);

        return buildMapper.toBuildResponseDTO(build);
    }

    @PostMapping("/{id}/reject")
    public BuildResponseDTO reject(
            @PathVariable @NonNull String id
    ) {
        Build build = buildService.rejectBuild(id);

        return buildMapper.toBuildResponseDTO(build);
    }

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

    @PostMapping("/{buildId}/issues/{issueId}/resolve")
    public BuildResponseDTO resolveIssue(
            @PathVariable @NonNull String buildId,
            @PathVariable @NonNull String issueId
    ) {
        Build build = buildService.resolveIssue(
                buildId,
                issueId
        );

        return buildMapper.toBuildResponseDTO(build);
    }

    @GetMapping("/{buildId}/issues")
    public List<IssueResponseDTO> getIssuesByBuild(
            @PathVariable @NonNull String buildId
    ) {
        return buildService.getIssuesByBuildId(buildId)
                .stream()
                .map(buildMapper::toIssueResponseDTO)
                .toList();
    }

    @GetMapping("/{buildId}/issues/{issueId}")
    public IssueResponseDTO getIssueById(
            @PathVariable @NonNull String buildId,
            @PathVariable @NonNull String issueId
    ) {
        Issue issue = buildService.getIssueById(
                buildId,
                issueId
        );

        return buildMapper.toIssueResponseDTO(issue);
    }
}