package com.example.projectboard.common.exception;

public class VerifyDuplicateException extends RuntimeException {

    public VerifyDuplicateException() {
        super();
    }

    public VerifyDuplicateException(String message) {
        super(message);
    }

    public VerifyDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
}
