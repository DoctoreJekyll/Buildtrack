package com.jose.buildtrack.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BuildTest {

    @Test
    void shouldStartWithCreatedStatus() {
        Build build = new Build(
                "build-001",
                new BuildVersion("1.0.0"),
                Platform.WINDOWS
        );

        assertEquals(BuildStatus.CREATED, build.getStatus());
    }

    @Test
    void shouldRejectBuildWhenVersionIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Build("build-001", null, Platform.WINDOWS)
        );
    }

    @Test
    void shouldStartValidationWhenBuildIsCreated() {

        Build build = new Build(
                "build-001",
                new BuildVersion("1.0.0"),
                Platform.WINDOWS
        );

        build.startValidation();

        assertEquals(BuildStatus.VALIDATING, build.getStatus());
    }

    @Test
    void shouldRejectApprovalWhenBuildHasOpenBlockerIssue()
    {
        Build build = new Build(
                "build-001",
                new BuildVersion("1.0.0"),
                Platform.WINDOWS
        );

        build.startValidation();
        build.addIssue(new Issue("ISSUE-001", "Blocker issue", IssueSeverity.BLOCKER));

        assertThrows(
                IllegalStateException.class,
                build::approve
        );
    }

    @Test
    void shouldApproveBuildWhenBlockerIssueIsResolved() {
        Build build = new Build(
                "build-001",
                new BuildVersion("1.0.0"),
                Platform.WINDOWS
        );

        build.startValidation();
        build.addIssue(new Issue("ISSUE-001", "Blocker issue", IssueSeverity.BLOCKER));
        build.resolveIssue("ISSUE-001");

        assertDoesNotThrow(build::approve);
    }

    @Test
    void shouldRejectDuplicatedIssueId() {
        Build build = new Build(
                "build-001",
                new BuildVersion("1.0.0"),
                Platform.WINDOWS
        );

        build.startValidation();
        build.addIssue(new Issue("ISSUE-001", "Blocker issue", IssueSeverity.BLOCKER));

        assertThrows(
                IllegalArgumentException.class,
                () -> build.addIssue(new Issue("ISSUE-001", "Another issue with same ID", IssueSeverity.LOW))
        );
    }

}