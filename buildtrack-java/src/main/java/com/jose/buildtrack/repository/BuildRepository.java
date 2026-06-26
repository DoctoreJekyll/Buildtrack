package com.jose.buildtrack.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.jose.buildtrack.domain.Build;

@Repository
public class BuildRepository {

    private final List<Build> builds = new ArrayList<>();

    public List<Build> findAll() {
        return List.copyOf(builds);
    }

    public Build save(Build build) {
        builds.add(build);
        return build;
    }

    public Optional<Build> findById(String id){
        for (Build build : builds) {
            if (build.getId().equals(id)) {
                return Optional.of(build);
            }
        }
        return Optional.empty();
    }

    public void delete(Build build) {
        builds.remove(build);
    }

}
