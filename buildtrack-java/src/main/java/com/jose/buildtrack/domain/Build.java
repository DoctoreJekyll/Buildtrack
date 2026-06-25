package com.jose.buildtrack.domain;

import java.util.ArrayList;
import java.util.List;

public class Build {

    private final String id;
    private final BuildVersion version;
    private BuildStatus status;
    private final Platform platform;
    private final List<Issue> issues;

    public Build(String id, BuildVersion version, Platform platform) {
        validateRequiredText(id, "ID");
        validateRequiredVersion(version);
        validateRequiredPlatform(platform);

        this.id = id;
        this.version = version;
        this.platform = platform;
        this.status = BuildStatus.CREATED;
        issues = new ArrayList<>();
    }

    private static void validateRequiredPlatform(Platform platform) {
        if (platform == null) {
            throw new IllegalArgumentException("Platform cannot be null");
        }
    }

    private static void validateRequiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be null, empty, or blank"
            );
        }
    }

    private static void validateRequiredVersion(BuildVersion version) {
        if (version == null) {
            throw new IllegalArgumentException("Version cannot be null");
        }
    }

    public String getId() {
        return id;
    }

    public BuildVersion getVersion() {
        return version;
    }

    public BuildStatus getStatus() {
        return status;
    }

    public Platform getPlatform() {
        return platform;
    }

    public List<Issue> getIssues() {
        return List.copyOf(issues);
    }

    public void startValidation() {
        if (status != BuildStatus.CREATED) {
            throw new IllegalStateException("Build must be in CREATED status to validate");
        }

        this.status = BuildStatus.VALIDATING;
    }

    public void approve() {
        if (status != BuildStatus.VALIDATING) {
            throw new IllegalStateException("Build must be in VALIDATING status to approve");
        }

        this.status = BuildStatus.APPROVED;
    }

    public void reject() {
        if (status != BuildStatus.VALIDATING) {
            throw new IllegalStateException("Build must be in VALIDATING status to reject");
        }

        this.status = BuildStatus.REJECTED;
    }

    public void addIssue(Issue issue) {
        if (issue == null) {
            throw new IllegalArgumentException("Issue cannot be null");
        }

        issues.add(issue);
    }

    public boolean hasOpenBlockerIssues() {

        for (Issue issue : issues) {

            if (issue.getSeverity() == IssueSeverity.BLOCKER
                    && issue.getStatus() == IssueStatus.OPEN) {

                return true;
            }
        }

        return false;
    }


}