package com.jose.buildtrack.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class BuildVersion {

    @Column(name = "version_value")
    private String value;

    protected BuildVersion() {

    }

    public BuildVersion(String value) {
        validateRequiredText(value, "Version");
        validateSemanticVersionFormat(value);

        this.value = value;
    }

    private static void validateRequiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be null, empty, or blank"
            );
        }
    }

    private static void validateSemanticVersionFormat(String value) {
        if (!value.matches("\\d+\\.\\d+\\.\\d+")) {
            throw new IllegalArgumentException(
                    "Version must follow the format number.number.number"
            );
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof BuildVersion)) {
            return false;
        }

        BuildVersion that = (BuildVersion) other;
        return this.value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}