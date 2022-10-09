package com.example.projectboard.common.handler;

import com.example.projectboard.common.exception.EntityNotFoundException;
import com.example.projectboard.common.exception.NoAuthorityToReadException;
import com.example.projectboard.common.exception.NoAuthorityToUpdateDeleteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class CommonExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({EntityNotFoundException.class, javax.persistence.EntityNotFoundException.class})
    public String entityNotFoundException(Model model,Exception exception) {
        log.error("handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());

        model.addAttribute("exception", exception.getMessage());

        return "error/exception";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public String illegalArgumentException(Model model, Exception exception) {
        log.error("handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());

        model.addAttribute("exception", exception.getMessage());

        return "error/exception";
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(NoAuthorityToUpdateDeleteException.class)
    public String noAuthorityToUpdateDeleteException(Model model, Exception exception) {
        log.error("handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());

        model.addAttribute("exception", exception.getMessage());

        return "error/exception";
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(NoAuthorityToReadException.class)
    public String noAuthorityToReadException(Model model, Exception exception) {
        log.error("handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());

        model.addAttribute("exception", exception.getMessage());

        return "error/exception";
    }
}
