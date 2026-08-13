package com.jose.buildtrack.exceptions;

public class IssueAlreadyExistsException
        extends RuntimeException {

    public IssueAlreadyExistsException(String issueId) {
        super(
                "An issue with ID '"
                        + issueId
                        + "' already exists");
    }
}