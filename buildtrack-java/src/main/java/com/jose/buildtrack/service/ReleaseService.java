package com.jose.buildtrack.service;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.jose.buildtrack.domain.Build;
import com.jose.buildtrack.domain.Release;
import com.jose.buildtrack.exceptions.BuildNotFoundException;
import com.jose.buildtrack.exceptions.ReleaseAlreadyExistsException;
import com.jose.buildtrack.exceptions.ReleaseNotFoundException;
import com.jose.buildtrack.repository.ReleaseRepository;

@Service
public class ReleaseService {

    private final ReleaseRepository releaseRepository;
    private final BuildService buildService;

    public ReleaseService(ReleaseRepository releaseRepository, BuildService buildService) {
        this.releaseRepository = releaseRepository;
        this.buildService = buildService;
    }

    public Release createRelease(String id, String name) {

        if (releaseRepository.findById(id).isPresent()) {
            throw new ReleaseAlreadyExistsException(id);
        }

        Release release = new Release(id, name);

        return releaseRepository.save(release);
    }

    public Optional<Release> findReleaseById(String releaseId) {
        return releaseRepository.findById(releaseId);
    }

    public Release getReleaseById(String releaseId) {
        return getReleaseOrThrow(releaseId);
    }

    public List<Release> getAllReleases() {
        return releaseRepository.findAll();
    }

    public void deleteRelease(String releaseId) {
        Release release = getReleaseOrThrow(releaseId);
        releaseRepository.delete(release);
    }

    public Release addBuildToRelease(String releaseId, @NonNull String buildId) {
        Release release = getReleaseOrThrow(releaseId);
        Optional<Build> optBuild = buildService.findBuildById(buildId);
        Build build = optBuild.orElseThrow(() -> new BuildNotFoundException(buildId));

        release.addBuild(build);
        return releaseRepository.save(release);
    }

    public Release prepareRelease(String releaseId) {
        Release release = getReleaseOrThrow(releaseId);
        release.startPreparation();
        return releaseRepository.save(release);
    }

    public Release publishRelease(String releaseId) {
        Release release = getReleaseOrThrow(releaseId);
        release.publish();
        return releaseRepository.save(release);
    }

    private Release getReleaseOrThrow(String releaseId) {
        return releaseRepository.findById(releaseId)
                .orElseThrow(() -> new ReleaseNotFoundException(releaseId));
    }
    
}