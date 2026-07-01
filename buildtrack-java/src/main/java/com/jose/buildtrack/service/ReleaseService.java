package com.jose.buildtrack.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.jose.buildtrack.domain.Release;
import com.jose.buildtrack.repository.ReleaseRepository;

@Service
public class ReleaseService {

    private final ReleaseRepository releaseRepository;

    public ReleaseService(ReleaseRepository releaseRepository) {
        this.releaseRepository = releaseRepository;
    }

    public Optional<Release> findReleaseById(String releaseId) {
        return releaseRepository.findById(releaseId);
    }

    public Release createRelease(String id, String name) {
        Release release = new Release(id, name);
        return releaseRepository.save(release);
    }

    public List<Release> getAllReleases() {
        return releaseRepository.findAll();
    }

    public void deleteRelease(String releaseId) {
        Release release = getReleaseOrThrow(releaseId);
        releaseRepository.delete(release);
    }

    private Release getReleaseOrThrow(String releaseId) {
        return releaseRepository.findById(releaseId)
                .orElseThrow(() -> new RuntimeException("Release not found: " + releaseId));
    }

}
