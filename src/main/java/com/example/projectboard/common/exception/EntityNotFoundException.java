package com.example.projectboard.common.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException() {
        super("존재하지 않는 엔티티입니다.");
        log.error("존재하지 않는 엔티티입니다: EntityNotFoundException");
    }

    public EntityNotFoundException(String message) {
        super(message);
        log.error("{}: EntityNotFoundException", message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
