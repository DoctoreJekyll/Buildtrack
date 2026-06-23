package com.jose.buildtrack.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BuildTest {

    @Test
    void shouldStartWithCreatedStatus() {
        Build build = new Build(
                "build-001",
                new BuildVersion("1.0.0")
        );

        assertEquals(BuildStatus.CREATED, build.getStatus());
    }

    @Test
    void shouldRejectBuildWhenVersionIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Build("build-001", null)
        );
    }

    @Test
    void shouldStartValidationWhenBuildIsCreated() {
        Build build = new Build(
                "build-001",
                new BuildVersion("1.0.0")
        );

        build.startValidation();

        assertEquals(BuildStatus.VALIDATING, build.getStatus());
    }
}