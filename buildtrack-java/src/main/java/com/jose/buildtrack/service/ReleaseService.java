package com.jose.buildtrack.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.jose.buildtrack.domain.Release;
import com.jose.buildtrack.exceptions.ReleaseAlreadyExistsException;
import com.jose.buildtrack.exceptions.ReleaseNotFoundException;
import com.jose.buildtrack.repository.ReleaseRepository;

@Service
public class ReleaseService {

    private final ReleaseRepository releaseRepository;

    public ReleaseService(ReleaseRepository releaseRepository) {
        this.releaseRepository = releaseRepository;
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

    private Release getReleaseOrThrow(String releaseId) {
        return releaseRepository.findById(releaseId)
                .orElseThrow(() -> new ReleaseNotFoundException(releaseId));
    }
}