package com.jose.buildtrack.domain;

import java.util.ArrayList;
import java.util.List;

public class Release {

    private final String id;
    private final String name;
    private final List<Build> builds;
    private ReleaseStatus status;

    public Release(String id, String name, List<Build> builds) {
        validateRequiredText(id, "ID");
        validateRequiredText(name, "Name");
        validateRequiredBuilds(builds);

        this.id = id;
        this.name = name;
        this.builds = new ArrayList<>(builds);
        this.status = ReleaseStatus.DRAFT;
    }

    private static void validateRequiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be null, empty, or blank"
            );
        }
    }

    private static void validateRequiredBuilds(List<Build> builds) {
        if (builds == null) {
            throw new IllegalArgumentException("Builds cannot be null");
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Build> getBuilds() {
        return List.copyOf(builds);
    }

    public ReleaseStatus getStatus() {
        return status;
    }

    public void addBuild(Build build) {
        if (build == null) {
            throw new IllegalArgumentException("Build cannot be null");
        }

        builds.add(build);
    }

    public void startPreparation() {
        if (status != ReleaseStatus.DRAFT) {
            throw new IllegalStateException("Release must be in DRAFT status");
        }

        if (!allBuildsApproved() || hasAnyBlockingIssues()) {
            throw new IllegalStateException(
                "Cannot move to READY: builds not approved or have blocking issues"
            );
        }

        this.status = ReleaseStatus.READY;
    }

    public void publish() {
        if (status != ReleaseStatus.READY) {
            throw new IllegalStateException("Release must be in READY status to publish");
        }

        if (!allBuildsApproved() || hasAnyBlockingIssues()) {
            throw new IllegalStateException("Release cannot be published due to validation rules");
        }

        this.status = ReleaseStatus.PUBLISHED;
    }

    private boolean hasAnyBlockingIssues() {
        for (Build build : builds) {
            for (Issue issue : build.getIssues()) {
                if (issue.getSeverity() == IssueSeverity.BLOCKER
                        && issue.getStatus() == IssueStatus.OPEN) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean allBuildsApproved() {
        for (Build build : builds) {
            if (build.getStatus() != BuildStatus.APPROVED) {
                return false;
            }
        }
        return true;
    }
}