package com.example.projectboard.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NoAuthorityToReadException extends RuntimeException {
    public NoAuthorityToReadException() {
        super("조회 권한이 없는 사용자입니다.");
    }

    public NoAuthorityToReadException(String message) {
        super(message);
    }

    public NoAuthorityToReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
