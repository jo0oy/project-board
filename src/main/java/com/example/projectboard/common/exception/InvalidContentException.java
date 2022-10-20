package com.example.projectboard.common.exception;

public class InvalidContentException extends RuntimeException {
    public InvalidContentException() {
    }

    public InvalidContentException(String message) {
        super(message);
    }

    public InvalidContentException(String message, Throwable cause) {
        super(message, cause);
    }
}
