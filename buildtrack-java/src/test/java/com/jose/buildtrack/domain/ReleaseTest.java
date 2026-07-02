package com.jose.buildtrack.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReleaseTest {

    @Test
    void shouldStartAsDraft() {
        Release release = new Release("release-001", "Release 1.0");

        assert(release.getStatus() == ReleaseStatus.DRAFT);
    }

    @Test
    void shouldRejectPreparationWhenReleaseHasNoBuilds() {
        Release release = new Release("release-001", "Release 1.0");

        assertThrows(
                IllegalStateException.class,
                () -> release.startPreparation()
        );
    }

    @Test
    void shouldRejectPreparationWhenBuildIsNotApproved () {
        Release release = new Release("release-001", "Release 1.0");
        Build build = new Build(
                "build-001",
                new BuildVersion("1.0.0"),
                Platform.WINDOWS
        );

        release.addBuild(build);

        assertThrows(
                IllegalStateException.class,
                () -> release.startPreparation()
        );
    }

    @Test
    void shouldMoveToReadyWhenAllBuildsAreApproved() {
        Release release = new Release("release-001", "Release 1.0");
        Build build = new Build(
                "build-001",
                new BuildVersion("1.0.0"),
                Platform.WINDOWS
        );

        build.startValidation();
        build.approve();

        release.addBuild(build);
        release.startPreparation();

        assert(release.getStatus() == ReleaseStatus.READY);
    }

    @Test
    void shouldPublishWhenReleaseIsReady(){
        Release release = new Release("release-001", "Release 1.0");
        Build build = new Build(
                "build-001",
                new BuildVersion("1.0.0"),
                Platform.WINDOWS
        );

        build.startValidation();
        build.approve();

        release.addBuild(build);
        release.startPreparation();
        release.publish();

        assert(release.getStatus() == ReleaseStatus.PUBLISHED);
    }

    @Test
    void shouldRejectPublishWhenReleaseIsDraft() {
        Release release = new Release("release-001", "Release 1.0");

        assertThrows(
                IllegalStateException.class,
                () -> release.publish()
        );
    }

    @Test 
    void shouldRejectDuplicatedBuildId() {
        Release release = new Release("release-001", "Release 1.0");
        Build build = new Build(
                "build-001",
                new BuildVersion("1.0.0"),
                Platform.WINDOWS
        );

        release.addBuild(build);

        assertThrows(
                IllegalArgumentException.class,
                () -> release.addBuild(build)
        );
    }

    @Test
    void shouldRejectAddingBuildWhenReleaseIsReady() {
        Release release = new Release("release-001", "Release 1.0");
        Build build = new Build(
                "build-001",
                new BuildVersion("1.0.0"),
                Platform.WINDOWS
        );

        build.startValidation();
        build.approve();

        release.addBuild(build);
        release.startPreparation();

        assertThrows(
                IllegalStateException.class,
                () -> release.addBuild(build)
        );
    }

}
