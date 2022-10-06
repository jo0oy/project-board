package com.example.projectboard.common.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UsernameNotFoundException extends RuntimeException {
    public UsernameNotFoundException() {
        super("유저를 찾을 수 없습니다.");
        log.error("유저를 찾을 수 없습니다. UsernameNotFoundException");
    }

    public UsernameNotFoundException(String message) {
        super(message);
        log.error("{}: {}", message, getClass().getSimpleName());
    }

    public UsernameNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
