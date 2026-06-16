package com.jose.buildtrack.domain;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class SoftwareProject {
    private final String id;
    private final String name;
    private final List<Build> builds;

    public SoftwareProject(String id, String name) {

        validateRequiredText(id, "ID");
        validateRequiredText(name, "Name");

        this.id = id;
        this.name = name;
        builds = new ArrayList<>();
    }

        private void validateRequiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be null, empty, or blank"
            );
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

    public void addBuild(Build build) {
        if (build == null) {
            throw new IllegalArgumentException("Build cannot be null");
        }
        verifyUniqueBuildId(build.getId());
        verifyUniqueBuildVersion(build.getVersion());
        builds.add(build);
    }

    private void verifyUniqueBuildId(String buildId) {
        for (Build build : builds) {
            if (build.getId().equals(buildId)) {
                throw new IllegalArgumentException("Build ID must be unique within the project");
            }
        }
    }

    private void verifyUniqueBuildVersion(String buildVersion) {
        for (Build build : builds) {
            if (build.getVersion().equals(buildVersion)) {
                throw new IllegalArgumentException("Build version must be unique within the project");
            }
        }
    }

    public Optional<Build> findBuildById(String buildId) {

        if (buildId == null || buildId.isBlank()) {
            throw new IllegalArgumentException("Build ID cannot be null, empty, or blank");
        }


        for (Build build : builds) {
            if (build.getId().equals(buildId)) {
                return Optional.of(build);
            }
        }
        return Optional.empty();
    }

    public void startBuildValidation(String buildId) {
        Build build = findBuildById(buildId)
                .orElseThrow(() -> new IllegalArgumentException("Build with ID " + buildId + " not found"));
        build.startValidation();
    }

}
