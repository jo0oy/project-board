package com.example.projectboard.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(HttpStatus.FORBIDDEN)
public class NoAuthorityToUpdateDeleteException extends RuntimeException{
    public NoAuthorityToUpdateDeleteException() {
        super("수정/삭제 권한이 없는 사용자입니다.");
        log.error("수정/삭제 권한이 없는 사용자입니다: NoAuthorityToUpdateDeleteException");
    }

    public NoAuthorityToUpdateDeleteException(String message) {
        super(message);
        log.error("{}: NoAuthorityToUpdateDeleteException", message);
    }

    public NoAuthorityToUpdateDeleteException(String message, Throwable cause) {
        super(message, cause);
        log.error("{}: NoAuthorityToUpdateDeleteException", message);
    }
}
