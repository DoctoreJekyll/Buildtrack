package com.jose.buildtrack.exceptions;

public class IssueNotFoundException extends RuntimeException {

    public IssueNotFoundException(String issueId) {
        super("Issue with ID " + issueId + " not found");
    }
}