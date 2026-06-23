package com.jose.buildtrack.domain;

public class Build {

    private final String id;
    private final BuildVersion version;
    private BuildStatus status;

    public Build(String id, BuildVersion version) {
        validateRequiredText(id, "ID");
        validateRequiredVersion(version);

        this.id = id;
        this.version = version;
        this.status = BuildStatus.CREATED;
    }

    private static void validateRequiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be null, empty, or blank"
            );
        }
    }

    private static void validateRequiredVersion(BuildVersion version) {
        if (version == null) {
            throw new IllegalArgumentException("Version cannot be null");
        }
    }

    public String getId() {
        return id;
    }

    public BuildVersion getVersion() {
        return version;
    }

    public BuildStatus getStatus() {
        return status;
    }

    public void startValidation() {
        if (status != BuildStatus.CREATED) {
            throw new IllegalStateException("Build must be in CREATED status to validate");
        }

        this.status = BuildStatus.VALIDATING;
    }

    public void approve() {
        if (status != BuildStatus.VALIDATING) {
            throw new IllegalStateException("Build must be in VALIDATING status to approve");
        }

        this.status = BuildStatus.APPROVED;
    }

    public void reject() {
        if (status != BuildStatus.VALIDATING) {
            throw new IllegalStateException("Build must be in VALIDATING status to reject");
        }

        this.status = BuildStatus.REJECTED;
    }
}