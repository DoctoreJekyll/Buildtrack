package com.jose.buildtrack.exceptions;

public class BuildAlreadyExistException extends RuntimeException {

    public BuildAlreadyExistException(Integer id) {
        super("Build with ID " + id + " already exists");
    }

}
