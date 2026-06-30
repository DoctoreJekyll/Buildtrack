package com.jose.buildtrack.exceptions;

public class InvalidIssueSeverityException extends RuntimeException {

    public InvalidIssueSeverityException(String severity) {
        super("Invalid issue severity: " + severity);
    }
}