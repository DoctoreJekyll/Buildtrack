package com.jose.buildtrack.exceptions;

public class ReleaseNotFoundException extends RuntimeException {

    public ReleaseNotFoundException(String releaseId) {
        super("Release with ID " + releaseId + " not found");
    }
}