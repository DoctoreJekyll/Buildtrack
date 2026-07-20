package com.jose.buildtrack.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jose.buildtrack.domain.Build;
import com.jose.buildtrack.domain.BuildStatus;
import com.jose.buildtrack.domain.Platform;

public interface BuildRepository extends JpaRepository<Build, String> {

    Page<Build> findByStatus(
            BuildStatus status,
            Pageable pageable
    );

    Page<Build> findByPlatform(
            Platform platform,
            Pageable pageable
    );

    Page<Build> findByStatusAndPlatform(
            BuildStatus status,
            Platform platform,
            Pageable pageable
    );
}