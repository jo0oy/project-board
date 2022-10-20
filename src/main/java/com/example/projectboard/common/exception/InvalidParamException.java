package com.example.projectboard.common.exception;

public class InvalidParamException extends RuntimeException {
    public InvalidParamException() {
        super("올바르지 않은 입력값입니다.");
    }

    public InvalidParamException(String message) {
        super(message);
    }

    public InvalidParamException(String message, Throwable cause) {
        super(message, cause);
    }
}
