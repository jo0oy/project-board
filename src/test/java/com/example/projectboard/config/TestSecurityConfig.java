package com.example.projectboard.config;

import com.example.projectboard.domain.users.UserAccount;
import com.example.projectboard.domain.users.UserAccountRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@Import(SecurityConfig.class)
public class TestSecurityConfig {

    @MockBean
    private UserAccountRepository userAccountRepository;

    @BeforeTestMethod
    public void securitySetUp() {
        given(userAccountRepository.findByUsername(eq("userTest"))).willReturn(Optional.of(UserAccount.of(
                "userTest",
                "userTest@pw",
                "테스트유저",
                "userTest@gmail.com",
                "010-1111-1000",
                UserAccount.RoleType.USER
        )));

        given(userAccountRepository.findByUsername(eq("userTest2"))).willReturn(Optional.of(UserAccount.of(
                "userTest2",
                "userTest2@pw",
                "테스트유저2",
                "userTest2@gmail.com",
                "010-2222-2000",
                UserAccount.RoleType.USER
        )));

        given(userAccountRepository.findByUsername(eq("adminTest"))).willReturn(Optional.of(UserAccount.of(
                "adminTest",
                "adminTest@pw",
                "테스트관리자",
                "adminTest@gmail.com",
                "010-3333-3000",
                UserAccount.RoleType.ADMIN
        )));
    }
}
