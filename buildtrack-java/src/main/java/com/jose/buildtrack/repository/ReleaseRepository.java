package com.jose.buildtrack.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jose.buildtrack.domain.Release;
import com.jose.buildtrack.domain.ReleaseStatus;

public interface ReleaseRepository
        extends JpaRepository<Release, String> {

    Page<Release> findByStatus(
            ReleaseStatus status,
            Pageable pageable
    );
}