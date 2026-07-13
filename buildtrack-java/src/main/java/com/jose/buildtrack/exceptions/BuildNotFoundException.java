package com.jose.buildtrack.exceptions;

public class BuildNotFoundException extends RuntimeException {
    public BuildNotFoundException(String buildId) {
        super("Build with ID " + buildId + " not found.");
    }

}
