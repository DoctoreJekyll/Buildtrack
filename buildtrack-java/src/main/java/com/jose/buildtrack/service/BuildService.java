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

import org.springframework.stereotype.Service;

@Service
public class BuildService {

    private final BuildRepository buildRepository;

    public BuildService(BuildRepository buildRepository) {
        this.buildRepository = buildRepository;
    }

    public Build createBuild(String id, String version, Platform platform) {
        if (buildRepository.findById(id).isPresent()) {
            throw new BuildAlreadyExistException(id);
        }

        Build build = new Build(id, new BuildVersion(version), platform);

        return buildRepository.save(build);
    }

    public Optional<Build> findBuildById(String buildId) {
        return buildRepository.findById(buildId);
    }

    public List<Build> getAllBuilds() {
        return buildRepository.findAll();
    }

    public Build startValidation(String buildId) {

        Build build = getBuildOrThrow(buildId);

        build.startValidation();

        return build;
    }

    private Build getBuildOrThrow(String buildId) {
        Build build = findBuildById(buildId)
                .orElseThrow(() -> new BuildNotFoundException(buildId));
        return build;
    }

    public Build approveBuild(String buildId) {
        Build build = getBuildOrThrow(buildId);

        build.approve();

        return build;
    }

    public Build rejectBuild(String buildId) {
        Build build = getBuildOrThrow(buildId);

        build.reject();

        return build;
    }

    public Build addIssueToBuild(String buildId, String issueId, String title, IssueSeverity severity) {
        Build build = getBuildOrThrow(buildId);

        Issue issue = new Issue(issueId, title, severity);

        build.addIssue(issue);

        return build;
    }

    public Build resolveIssue(String buildId, String issueId) {
        Build build = getBuildOrThrow(buildId);

        build.resolveIssue(issueId);

        return build;
    }

    public List<Issue> getIssuesByBuildId(String buildId) {
        Build build = getBuildOrThrow(buildId);

        return build.getIssues();
    }

    public Issue getIssueById(String buildId, String issueId) {
        Build build = getBuildOrThrow(buildId);
        
        return build.findIssueById(issueId)
                .orElseThrow(() -> new IssueNotFoundException(issueId));
    }
    
}