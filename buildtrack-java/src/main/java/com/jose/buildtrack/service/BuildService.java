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

    public void startValidation(String buildId) {

        Build build = findBuildById(buildId)
                .orElseThrow(() -> new IllegalArgumentException("Build not found"));

        build.startValidation();
    }

    public void approveBuild(String buildId) {

        Build build = findBuildById(buildId)
                .orElseThrow(() -> new IllegalArgumentException("Build not found"));

        build.approve();
    }

    public void rejectBuild(String buildId) {

        Build build = findBuildById(buildId)
                .orElseThrow(() -> new IllegalArgumentException("Build not found"));

        build.reject();
    }

    public void listBuilds() {
        for (Build build : builds) {
            System.out.println("Build ID: " + build.getId() + ", Version: " + build.getVersion() + ", Status: " + build.getStatus());
        }
    }
}