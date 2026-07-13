package com.jose.buildtrack.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BuildVersionTest {

    @Test
    void shouldCreateBuildVersionWhenValueHasValidFormat() {
        BuildVersion version = new BuildVersion("1.0.0");
        assertEquals("1.0.0", version.getValue());
    }

    @Test
    void shouldRejectBuildVersionWhenValueIsInvalidFormat() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BuildVersion("release")
        );
    }

    @Test
    void shouldStartBuildAsCreated() {
        // Arrange
        BuildVersion version = new BuildVersion("1.0.0");

        // Act
        Build build = new Build("build-001", version, Platform.WINDOWS);

        // Assert
        assertEquals(BuildStatus.CREATED, build.getStatus());
    }

}
