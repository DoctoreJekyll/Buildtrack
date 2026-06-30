package com.jose.buildtrack.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jose.buildtrack.domain.Build;
import com.jose.buildtrack.domain.Issue;
import com.jose.buildtrack.domain.IssueSeverity;
import com.jose.buildtrack.domain.Platform;
import com.jose.buildtrack.dto.BuildResponseDTO;
import com.jose.buildtrack.dto.CreateBuildRequestDTO;
import com.jose.buildtrack.dto.CreateIssueRequestDTO;
import com.jose.buildtrack.dto.IssueResponseDTO;
import com.jose.buildtrack.exceptions.BuildNotFoundException;
import com.jose.buildtrack.exceptions.InvalidPlatformException;
import com.jose.buildtrack.service.BuildService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/builds")
public class BuildController {
    private final BuildService buildService;

    public BuildController(BuildService buildService) {
        this.buildService = buildService;
    }

    @PostMapping
    public BuildResponseDTO create(@Valid @RequestBody CreateBuildRequestDTO request)
    {
        Build build = buildService.createBuild(
            request.id(), 
            request.version(), 
            parsePlatform(request.platform())
        );

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

    @PostMapping("/{id}/issues")
    public BuildResponseDTO addIssue(
            @PathVariable String id,
            @Valid @RequestBody CreateIssueRequestDTO request
    ) {
        Build build = buildService.addIssueToBuild(
                id,
                request.id(),
                request.title(),
                parseIssueSeverity(request.severity())
        );
    
        return toResponseDTO(build);
    }

    @PostMapping("/{buildId}/issues/{issueId}/resolve")
    public BuildResponseDTO resolveIssue(@PathVariable String buildId, @PathVariable String issueId) {
        Build build = buildService.resolveIssue(buildId, issueId);
        return toResponseDTO(build);
    }

    @GetMapping("/{buildId}/issues")
    public List<IssueResponseDTO> getIssuesByBuild(@PathVariable String buildId) {
        return buildService.getIssuesByBuildId(buildId)
                .stream()
                .map(this::toIssueResponseDTO)
                .toList();
    }

    @GetMapping("/{buildId}/issues/{issueId}")
    public IssueResponseDTO getIssueById(@PathVariable String buildId,@PathVariable String issueId) {
        Issue issue = buildService.getIssueById(buildId, issueId);

        return toIssueResponseDTO(issue);
    }

    private IssueSeverity parseIssueSeverity(String severity) {
        try {
            return IssueSeverity.valueOf(severity.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid issue severity: " + severity);
        }
    }

    private BuildResponseDTO toResponseDTO(Build build) {
        return new BuildResponseDTO(
            build.getId(),
            build.getVersion().getValue(),
            build.getPlatform().name(),
            build.getStatus().name(),
                        build.getIssues()
                    .stream()
                    .map(this::toIssueResponseDTO)
                    .toList()
            
            
        );
    }

    private Platform parsePlatform(String platform) {
        try {
            return Platform.valueOf(platform.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new InvalidPlatformException(platform);
        }
    }

    private IssueResponseDTO toIssueResponseDTO(Issue issue) {
        return new IssueResponseDTO(
                issue.getId(),
                issue.getTitle(),
                issue.getSeverity().name(),
                issue.getStatus().name()
        );
}

}
