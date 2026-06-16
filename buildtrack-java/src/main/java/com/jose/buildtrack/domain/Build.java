package com.jose.buildtrack.domain;

public class Build {
    private final String id;
    private final String version;
    private BuildStatus status;

    public Build(String id, String version) {
        validateRequiredText(id, "ID");
        validateRequiredText(version, "Version");

        this.id = id;
        this.version = version;
        status = BuildStatus.CREATED;
    }

    private void validateRequiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be null, empty, or blank"
            );
        }
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public BuildStatus getStatus() {
        return status;
    }

    public void startValidation() {
        if (status != BuildStatus.CREATED) {
            throw new IllegalStateException("Build must be in CREATED status to validate");
        }
        status = BuildStatus.VALIDATING;
    }



    public void approve() {
        if (status != BuildStatus.VALIDATING) {
            throw new IllegalStateException("Build must be in VALIDATING status to approve");
        }
        status = BuildStatus.APPROVED;
    }

    public void reject() {
        if (status != BuildStatus.VALIDATING) {
            throw new IllegalStateException("Build must be in VALIDATING status to reject");
        }
        status = BuildStatus.REJECTED;
    }

}
