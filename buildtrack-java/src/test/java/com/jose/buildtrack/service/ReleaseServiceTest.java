package com.jose.buildtrack.service;

import com.jose.buildtrack.domain.Platform;
import com.jose.buildtrack.domain.Release;
import com.jose.buildtrack.domain.ReleaseStatus;
import com.jose.buildtrack.exceptions.BuildNotFoundException;
import com.jose.buildtrack.exceptions.ReleaseAlreadyExistsException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReleaseServiceTest {

    @Autowired
    private BuildService buildService;

    @Autowired
    private ReleaseService releaseService;

    @Test
    void shouldCreateRelease() {
        Release release = releaseService.createRelease(
                "release-service-001",
                "Release 1.0"
        );

        assertEquals("release-service-001", release.getId());
        assertEquals("Release 1.0", release.getName());
        assertEquals(ReleaseStatus.DRAFT, release.getStatus());
    }

    @Test
    void shouldRejectDuplicatedReleaseId() {
        releaseService.createRelease(
                "release-service-duplicate",
                "Release 1.0"
        );

        assertThrows(
                ReleaseAlreadyExistsException.class,
                () -> releaseService.createRelease(
                        "release-service-duplicate",
                        "Another Release"
                )
        );
    }

    @Test
    void shouldAddExistingBuildToRelease() {
        buildService.createBuild(
                "build-service-001",
                "1.0.0",
                Platform.WINDOWS
        );

        releaseService.createRelease(
                "release-service-add-build",
                "Release 1.0"
        );

        Release release = releaseService.addBuildToRelease(
                "release-service-add-build",
                "build-service-001"
        );

        assertEquals(1, release.getBuilds().size());
        assertEquals("build-service-001", release.getBuilds().get(0).getId());
    }

    @Test
    void shouldRejectAddingUnknownBuildToRelease() {
        releaseService.createRelease(
                "release-service-unknown-build",
                "Release 1.0"
        );

        assertThrows(
                BuildNotFoundException.class,
                () -> releaseService.addBuildToRelease(
                        "release-service-unknown-build",
                        "build-unknown"
                )
        );
    }

    @Test
    void shouldPrepareReleaseWhenBuildIsApproved() {
        buildService.createBuild(
                "build-service-approved",
                "1.0.0",
                Platform.WINDOWS
        );

        buildService.startValidation("build-service-approved");
        buildService.approveBuild("build-service-approved");

        releaseService.createRelease(
                "release-service-prepare",
                "Release 1.0"
        );

        releaseService.addBuildToRelease(
                "release-service-prepare",
                "build-service-approved"
        );

        Release release = releaseService.prepareRelease("release-service-prepare");

        assertEquals(ReleaseStatus.READY, release.getStatus());
    }

    @Test
    void shouldPublishReleaseAfterPreparation() {
        buildService.createBuild(
                "build-service-publish",
                "1.0.0",
                Platform.WINDOWS
        );

        buildService.startValidation("build-service-publish");
        buildService.approveBuild("build-service-publish");

        releaseService.createRelease(
                "release-service-publish",
                "Release 1.0"
        );

        releaseService.addBuildToRelease(
                "release-service-publish",
                "build-service-publish"
        );

        releaseService.prepareRelease("release-service-publish");

        Release release = releaseService.publishRelease("release-service-publish");

        assertEquals(ReleaseStatus.PUBLISHED, release.getStatus());
    }
}