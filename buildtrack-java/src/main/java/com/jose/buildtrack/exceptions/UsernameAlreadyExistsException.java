package com.jose.buildtrack.exceptions;

public class UsernameAlreadyExistsException
        extends RuntimeException {

    public UsernameAlreadyExistsException(String username) {
        super(
                "A user with username '"
                + username
                + "' already exists"
        );
    }
}