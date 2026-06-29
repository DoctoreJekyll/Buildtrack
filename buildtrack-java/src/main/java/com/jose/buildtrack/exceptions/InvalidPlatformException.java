package com.jose.buildtrack.exceptions;

public class InvalidPlatformException extends RuntimeException {

    public InvalidPlatformException(String platform) {
        super("Invalid platform: " + platform);
    }
}