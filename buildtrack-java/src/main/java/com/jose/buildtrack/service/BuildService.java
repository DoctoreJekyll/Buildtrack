package com.jose.buildtrack.service;

import com.jose.buildtrack.domain.Build;
import com.jose.buildtrack.domain.BuildVersion;
import com.jose.buildtrack.domain.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class BuildService {

    private final List<Build> builds = new ArrayList<>();

    public Build createBuild(String id, String version, Platform platform) {

        Build build = new Build(id, new BuildVersion(version), platform);

        builds.add(build);

        return build;
    }

    public Optional<Build> findBuildById(String buildId) {

        for (Build build : builds) {
            if (build.getId().equals(buildId)) {
                return Optional.of(build);
            }
        }

        return Optional.empty();
    }

    public List<Build> getAllBuilds() {
        return List.copyOf(builds);
    }

    public Build startValidation(String buildId) {

        Build build = getBuild(buildId);

        build.startValidation();

        return build;
    }

    private Build getBuild(String buildId) {
        Build build = findBuildById(buildId)
                .orElseThrow(() -> new IllegalArgumentException("Build not found"));
        return build;
    }

    public Build approveBuild(String buildId) {

        Build build = getBuild(buildId);

        build.approve();

        return build;
    }

    public Build rejectBuild(String buildId) {

        Build build = getBuild(buildId);

        build.reject();

        return build;
    }
}