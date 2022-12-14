package com.example.projectboard.config;

import com.example.projectboard.security.PrincipalUserAccount;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorAware() {

        return () ->
                Optional.ofNullable(SecurityContextHolder.getContext())
                        .map(SecurityContext::getAuthentication)
                        .filter(authentication -> (authentication.isAuthenticated()
                                && !(authentication instanceof AnonymousAuthenticationToken)))
                        .map(Authentication::getPrincipal)
                        .map(PrincipalUserAccount.class::cast)
                        .map(PrincipalUserAccount::getUsername);
    }
}

