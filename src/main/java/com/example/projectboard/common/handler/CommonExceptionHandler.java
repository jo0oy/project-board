package com.example.projectboard.common.handler;

import com.example.projectboard.common.exception.EntityNotFoundException;
import com.example.projectboard.common.exception.NoAuthorityToReadException;
import com.example.projectboard.common.exception.NoAuthorityToUpdateDeleteException;
import com.example.projectboard.common.exception.UsernameNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.format.DateTimeParseException;

@Slf4j
@ControllerAdvice
public class CommonExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({EntityNotFoundException.class, javax.persistence.EntityNotFoundException.class})
    public String entityNotFoundException(Model model,Exception exception) {
        log.error("handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());

        var exceptionMessage = (exception instanceof EntityNotFoundException) ?
                exception.getMessage() : "존재하지 않는 정보입니다.";

        model.addAttribute("exception", exceptionMessage);

        return "error/error-page";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UsernameNotFoundException.class)
    public String usernameNotFoundException(Model model,Exception exception) {
        log.error("handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());

        model.addAttribute("exception", exception.getMessage());

        return "error/error-page";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public String illegalArgumentException(Model model, Exception exception) {
        log.error("handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());

        model.addAttribute("exception", exception.getMessage());

        return "error/error-page";
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ModelAndView dateTimeParseException(Exception exception,
                                               HttpServletRequest request,
                                               RedirectAttributes redirectAttributes) {
        log.error("handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());

        redirectAttributes.addFlashAttribute("exception", "'작성일' 요청값을 올바르게 입력해주세요.");

        return new ModelAndView("redirect:" + request.getRequestURI() + "?error");
    }

    // 접근 권한 에러
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({NoAuthorityToUpdateDeleteException.class, NoAuthorityToReadException.class})
    public String accessDeniedException(Model model, Exception exception) {
        log.error("handling {}: {}", exception.getClass().getSimpleName(), exception.getMessage());

        model.addAttribute("exception", exception.getMessage());

        return "error/error-page";
    }
}
