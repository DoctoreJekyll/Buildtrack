package com.jose.buildtrack.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.jose.buildtrack.domain.Release;

@Repository
public class ReleaseRepository {

    private final List<Release> releases = new ArrayList<>();

    public List<Release> findAll() {
        return List.copyOf(releases);
    }

    public Optional<Release> findById(String id) {
        for (Release release : releases) {
            if (release.getId().equals(id)) {
                return Optional.of(release);
            }
        }
        return Optional.empty();
    }

    public Release save(Release release) {
        for (int i = 0; i < releases.size(); i++) {
            if (releases.get(i).getId().equals(release.getId())) {
                releases.set(i, release);
                return release;
            }
        }

        releases.add(release);
        return release;
    }

    public void delete(Release release) {
        releases.remove(release);
    }

}
