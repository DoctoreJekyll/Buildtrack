package com.jose.buildtrack.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jose.buildtrack.domain.Release;
import com.jose.buildtrack.domain.ReleaseStatus;
import com.jose.buildtrack.exceptions.BuildNotFoundException;
import com.jose.buildtrack.exceptions.ReleaseAlreadyExistsException;
import com.jose.buildtrack.repository.ReleaseRepository;

public class ReleaseServiceTest {

    @Autowired
    BuildService buildService;
    

    private ReleaseService createReleaseService(BuildService buildService) {
        ReleaseRepository releaseRepository = new ReleaseRepository();
        return new ReleaseService(releaseRepository, buildService);
    }

    @Test
    void shouldCreateRelease() {
        // Arrange
        ReleaseService releaseService = createReleaseService(buildService);

        // Act
        Release release = releaseService.createRelease("release-001", "Release 1.0");

        // Assert
        assertEquals("release-001", release.getId());
        assertEquals("Release 1.0", release.getName());
        assertEquals(ReleaseStatus.DRAFT, release.getStatus());
    }

    @Test
    void shouldRejectDuplicatedReleaseId() {
        // Arrange
        ReleaseService releaseService = createReleaseService(buildService);

        releaseService.createRelease("release-001", "Release 1.0");

        // Act + Assert
        assertThrows(
                ReleaseAlreadyExistsException.class,
                () -> releaseService.createRelease("release-001", "Another Release")
        );
    }

    @Test
    void shouldAddExistingBuildToRelease() {
        // Arrange
        ReleaseService releaseService = createReleaseService(buildService);

        buildService.createBuild("build-001", "1.0.0", com.jose.buildtrack.domain.Platform.WINDOWS);
        releaseService.createRelease("release-001", "Release 1.0");

        // Act
        Release release = releaseService.addBuildToRelease("release-001", "build-001");

        // Assert
        assertEquals(1, release.getBuilds().size());
        assertEquals("build-001", release.getBuilds().get(0).getId());
    }

    @Test
    void shouldRejectAddingUnknownBuildToRelease() {
        // Arrange
        ReleaseService releaseService = createReleaseService(buildService);

        releaseService.createRelease("release-001", "Release 1.0");

        // Act + Assert
        assertThrows(
                BuildNotFoundException.class,
                () -> releaseService.addBuildToRelease("release-001", "build-999")
        );
    }

    @Test
    void shouldPrepareReleaseWhenBuildIsApproved() {
        // Arrange
        ReleaseService releaseService = createReleaseService(buildService);

        buildService.createBuild("build-001", "1.0.0", com.jose.buildtrack.domain.Platform.WINDOWS);
        buildService.startValidation("build-001");
        buildService.approveBuild("build-001");

        releaseService.createRelease("release-001", "Release 1.0");
        releaseService.addBuildToRelease("release-001", "build-001");

        // Act
        Release release = releaseService.prepareRelease("release-001");

        // Assert
        assertEquals(ReleaseStatus.READY, release.getStatus());
    }

    @Test
    void shouldPublishReleaseAfterPreparation() {
        // Arrange
        ReleaseService releaseService = createReleaseService(buildService);

        buildService.createBuild("build-001", "1.0.0", com.jose.buildtrack.domain.Platform.WINDOWS);
        buildService.startValidation("build-001");
        buildService.approveBuild("build-001");

        releaseService.createRelease("release-001", "Release 1.0");
        releaseService.addBuildToRelease("release-001", "build-001");
        releaseService.prepareRelease("release-001");

        // Act
        Release release = releaseService.publishRelease("release-001");

        // Assert
        assertEquals(ReleaseStatus.PUBLISHED, release.getStatus());
    }


    
}
