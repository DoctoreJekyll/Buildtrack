package com.jose.buildtrack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jose.buildtrack.domain.Build;

public interface BuildRepository extends JpaRepository<Build, String> {
}
