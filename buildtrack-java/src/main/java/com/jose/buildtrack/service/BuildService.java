package com.jose.buildtrack.service;

import com.jose.buildtrack.domain.Build;
import com.jose.buildtrack.domain.BuildVersion;
import com.jose.buildtrack.domain.Platform;
import com.jose.buildtrack.exceptions.BuildNotFoundException;
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
}