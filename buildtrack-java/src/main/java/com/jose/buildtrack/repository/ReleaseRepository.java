package com.jose.buildtrack.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.jose.buildtrack.domain.Release;


public interface ReleaseRepository  extends JpaRepository<Release, String> {

}
