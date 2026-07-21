package com.jose.buildtrack.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jose.buildtrack.domain.AppUser;

public interface AppUserRepository
        extends JpaRepository<AppUser, Long> {

    boolean existsByUsername(String username);

    Optional<AppUser> findByUsername(String username);
}