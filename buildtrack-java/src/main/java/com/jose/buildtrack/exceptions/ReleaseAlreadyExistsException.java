package com.jose.buildtrack.exceptions;

public class ReleaseAlreadyExistsException extends RuntimeException {

    public ReleaseAlreadyExistsException(String releaseId) {
        super("Release with ID " + releaseId + " already exists");
    }
}