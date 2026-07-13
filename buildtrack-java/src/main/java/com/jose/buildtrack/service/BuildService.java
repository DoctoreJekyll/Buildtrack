package com.jose.buildtrack.service;

import com.jose.buildtrack.domain.Build;
import com.jose.buildtrack.domain.BuildVersion;
import com.jose.buildtrack.domain.Issue;
import com.jose.buildtrack.domain.IssueSeverity;
import com.jose.buildtrack.domain.Platform;
import com.jose.buildtrack.exceptions.BuildAlreadyExistException;
import com.jose.buildtrack.exceptions.BuildNotFoundException;
import com.jose.buildtrack.exceptions.IssueNotFoundException;
import com.jose.buildtrack.repository.BuildRepository;


import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class BuildService {

    private final BuildRepository buildRepository;

    public BuildService(BuildRepository buildRepository) {
        this.buildRepository = buildRepository;
    }

    public Build createBuild(@NonNull String id, String version, Platform platform) {
        if (buildRepository.findById(id).isPresent()) {
            throw new BuildAlreadyExistException(id);
        }

        Build build = new Build(id, new BuildVersion(version), platform);

        return buildRepository.save(build);
    }

    public Optional<Build> findBuildById(@NonNull String buildId) {
        return buildRepository.findById(buildId);
    }

    public List<Build> getAllBuilds() {
        return buildRepository.findAll();
    }

    public Build startValidation(@NonNull String buildId) {

        Build build = getBuildOrThrow(buildId);

        build.startValidation();

        return buildRepository.save(build);
    }

    public Build getBuildOrThrow(@NonNull String buildId) {
        Build build = findBuildById(buildId)
                .orElseThrow(() -> new BuildNotFoundException(buildId));
        return build;
    }

    public Build approveBuild(@NonNull String buildId) {
        Build build = getBuildOrThrow(buildId);

        build.approve();

        return buildRepository.save(build);
    }

    public Build rejectBuild(@NonNull String buildId) {
        Build build = getBuildOrThrow(buildId);

        build.reject();

        return buildRepository.save(build);
    }

    public Build addIssueToBuild(@NonNull String buildId, String issueId, String title, IssueSeverity severity) {
        Build build = getBuildOrThrow(buildId);

        Issue issue = new Issue(issueId, title, severity);

        build.addIssue(issue);

        return buildRepository.save(build);
    }

    public Build resolveIssue(@NonNull String buildId, String issueId) {
        Build build = getBuildOrThrow(buildId);

        build.resolveIssue(issueId);

        return buildRepository.save(build);
    }

    public List<Issue> getIssuesByBuildId(@NonNull String buildId) {
        Build build = getBuildOrThrow(buildId);

        return build.getIssues();
    }

    public Issue getIssueById(@NonNull String buildId, String issueId) {
        Build build = getBuildOrThrow(buildId);

        return build.findIssueById(issueId)
                .orElseThrow(() -> new IssueNotFoundException(issueId));
    }
    
}