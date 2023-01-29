package com.example.projectboard.config;

import com.example.projectboard.domain.users.UserAccountCacheRepository;
import com.example.projectboard.domain.users.UserAccountInfoMapper;
import com.example.projectboard.domain.users.UserAccountRepository;
import com.example.projectboard.security.PrincipalUserDetailsService;
import com.example.projectboard.security.WebAccessDeniedHandler;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new WebAccessDeniedHandler();
    }

    @Bean
    public UserDetailsService userDetailsService(UserAccountRepository userAccountRepository,
                                                 UserAccountCacheRepository redisRepository,
                                                 UserAccountInfoMapper mapper) {
        return new PrincipalUserDetailsService(userAccountRepository, redisRepository, mapper);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .mvcMatchers(
                                HttpMethod.GET,
                                "/",
                                "/articles",
                                "/articles/**",
                                "/hashtags",
                                "/auth/login",
                                "/accounts/sign-up/**",
                                "/api/v1/files/**"
                        ).permitAll()
                        .mvcMatchers(
                                HttpMethod.POST,
                                "/accounts/sign-up",
                                "/auth/login-proc"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin()
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login-proc")
                .defaultSuccessUrl("/")
                .failureUrl("/auth/login?error")
                .permitAll()
                .and()
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                        .invalidateHttpSession(true)
                )
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler())
                .and()
                .build();
    }
}
