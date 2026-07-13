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
import com.jose.buildtrack.dto.BuildResponseDTO;
import com.jose.buildtrack.dto.CreateBuildRequestDTO;
import com.jose.buildtrack.dto.CreateIssueRequestDTO;
import com.jose.buildtrack.dto.IssueResponseDTO;
import com.jose.buildtrack.exceptions.BuildNotFoundException;
import com.jose.buildtrack.mapper.BuildMapper;
import com.jose.buildtrack.service.BuildService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/builds")
public class BuildController {

    private final BuildService buildService;
    private final BuildMapper buildMapper;

    public BuildController(BuildService buildService, BuildMapper buildMapper) {
        this.buildService = buildService;
        this.buildMapper = buildMapper;
    }

    @PostMapping
    public BuildResponseDTO create(@Valid @RequestBody CreateBuildRequestDTO request) {

        Build build = buildService.createBuild(
                request.id(),
                request.version(),
                buildMapper.toPlatform(request.platform())
        );

        return buildMapper.toBuildResponseDTO(build);
    }

    @GetMapping("/{id}")
    public BuildResponseDTO getBuild(@PathVariable String id) {

        Build build = buildService.findBuildById(id)
                .orElseThrow(() -> new BuildNotFoundException(id));

        return buildMapper.toBuildResponseDTO(build);
    }

    @GetMapping
    public List<BuildResponseDTO> getAllBuilds() {

        return buildService.getAllBuilds()
                .stream()
                .map(buildMapper::toBuildResponseDTO)
                .toList();
    }

    @PostMapping("/{id}/validate")
    public BuildResponseDTO validate(@PathVariable String id) {

        Build build = buildService.startValidation(id);

        return buildMapper.toBuildResponseDTO(build);
    }

    @PostMapping("/{id}/approve")
    public BuildResponseDTO approve(@PathVariable String id) {

        Build build = buildService.approveBuild(id);

        return buildMapper.toBuildResponseDTO(build);
    }

    @PostMapping("/{id}/reject")
    public BuildResponseDTO reject(@PathVariable String id) {

        Build build = buildService.rejectBuild(id);

        return buildMapper.toBuildResponseDTO(build);
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
                buildMapper.toIssueSeverity(request.severity())
        );

        return buildMapper.toBuildResponseDTO(build);
    }

    @PostMapping("/{buildId}/issues/{issueId}/resolve")
    public BuildResponseDTO resolveIssue(
            @PathVariable String buildId,
            @PathVariable String issueId
    ) {
        Build build = buildService.resolveIssue(buildId, issueId);

        return buildMapper.toBuildResponseDTO(build);
    }

    @GetMapping("/{buildId}/issues")
    public List<IssueResponseDTO> getIssuesByBuild(@PathVariable String buildId) {

        return buildService.getIssuesByBuildId(buildId)
                .stream()
                .map(buildMapper::toIssueResponseDTO)
                .toList();
    }

    @GetMapping("/{buildId}/issues/{issueId}")
    public IssueResponseDTO getIssueById(
            @PathVariable String buildId,
            @PathVariable String issueId
    ) {
        Issue issue = buildService.getIssueById(buildId, issueId);

        return buildMapper.toIssueResponseDTO(issue);
    }
}