package com.example.projectboard.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class WebAccessDeniedHandler implements AccessDeniedHandler {
    private static final String ERROR_MSG = "message";
    private static final String LOGGED_IN = "loggedIn";

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.info("AccessDeniedHandler 로직 실행!");

        response.setStatus(HttpStatus.FORBIDDEN.value());

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        // 로그인된 일반 회원 혹인 관리자
        if (authentication != null && authentication.isAuthenticated()
                && (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))
                || authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))) {
            log.info("authorities={}", authentication.getAuthorities());
            request.setAttribute(ERROR_MSG, "요청에 대한 접근 권한이 없는 회원입니다.");
            request.setAttribute(LOGGED_IN, true);
        } else {
            request.setAttribute(ERROR_MSG, "로그인이 필요한 서비스입니다.");
            request.setAttribute(LOGGED_IN, false);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            SecurityContextHolder.clearContext();
        }

        request.getRequestDispatcher("/error/denied").forward(request, response);
    }
}
