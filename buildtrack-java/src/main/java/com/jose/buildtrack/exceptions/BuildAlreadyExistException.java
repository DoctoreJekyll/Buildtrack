package com.jose.buildtrack.exceptions;

public class BuildAlreadyExistException extends RuntimeException {

    public BuildAlreadyExistException(String buildId) {
        super("Build with ID " + buildId + " already exists");
    }

}
