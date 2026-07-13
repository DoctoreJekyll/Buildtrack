package com.jose.buildtrack.exceptions;

public class BuildNotFoundException extends RuntimeException {
    public BuildNotFoundException(Integer buildId) {
        super("Build with ID " + buildId + " not found.");
    }

}
