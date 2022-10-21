package com.example.projectboard.interfaces.web.users;

import com.example.projectboard.application.users.UserAccountQueryService;
import com.example.projectboard.common.exception.NoAuthorityToUpdateDeleteException;
import com.example.projectboard.config.TestSecurityConfig;
import com.example.projectboard.domain.users.UserAccount;
import com.example.projectboard.domain.users.UserAccountInfo;
import com.example.projectboard.interfaces.dto.users.UserAccountDtoMapper;
import com.example.projectboard.interfaces.dto.users.UserAccountDtoMapperImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestSecurityConfig.class)
@ComponentScan(basePackageClasses = {UserAccountDtoMapper.class, UserAccountDtoMapperImpl.class})
@AutoConfigureMockMvc
@WebMvcTest(controllers = UserAccountViewController.class)
public class UserAccountViewControllerTest {

    @Autowired
    private MockMvc mvc;

    @SpyBean
    private UserAccountDtoMapper userAccountDtoMapper;

    @MockBean
    private UserAccountQueryService userAccountQueryService;

    @DisplayName("[성공][view][GET] 회원가입 페이지")
    @Test
    void givenNothing_whenRequestingSignUpView_thenReturnsSignUpView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/accounts/sign-up"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("users/sign-up"));
    }

    @DisplayName("[성공][view][GET] 회원가입 성공 페이지")
    @Test
    void givenNothing_whenRequestingSignUpSuccessView_thenReturnsSignUpSuccessView() throws Exception {
        // Given
        // When & Then
        mvc.perform(get("/accounts/sign-up/success"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("users/sign-up-success"));
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][view][GET] Account me 페이지 - 인증된 사용자(ROLE_USER)")
    @Test
    void givenNothingWithRoleUserAccount_whenRequestingMeView_thenReturnsMeView() throws Exception {
        // Given
        var username = "userTest";
        var userInfo = getUserInfo(1, username);

        given(userAccountQueryService.getUserAccountInfo(username)).willReturn(userInfo);

        // When & Then
        mvc.perform(get("/accounts/me"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("users/user-info"))
                .andExpect(model().attributeExists("userInfo"));

        then(userAccountQueryService).should().getUserAccountInfo(anyString());
    }

    @DisplayName("[실패][view][GET] Account me 페이지 - 인증되지 않은 사용자")
    @Test
    void givenNothingNotAuthenticatedUser_whenRequestingMeView_thenRedirectsToLoginView() throws Exception {
        // Given
        // When & Then
        mvc.perform(get("/accounts/me"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][view][GET] 로그인된 회원정보 수정 페이지 - 인증된 사용자(ROLE_USER)")
    @Test
    void givenNothingWithRoleUserAccount_whenRequestingMeEditView_thenReturnsMeEditView() throws Exception {
        // Given
        var username = "userTest";
        var userInfo = getUserInfo(1, username);

        given(userAccountQueryService.getUserAccountInfo(username)).willReturn(userInfo);

        // When & Then
        mvc.perform(get("/accounts/me/edit"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("users/edit-form"))
                .andExpect(model().attributeExists("updateForm"));

        then(userAccountQueryService).should().getUserAccountInfo(anyString());
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][view][GET] {username}의 회원정보 페이지 - 인증된 사용자 본인(ROLE_USER)")
    @Test
    void givenUsernameWithAuthorizedUserAccount_whenRequestingUserInfoView_thenReturnsUserInfoView() throws Exception {
        // Given
        var username = "userTest";
        var principalUsername = "userTest";
        var userInfo = getUserInfo(1, username);

        given(userAccountQueryService.getUserAccountInfo(username, principalUsername)).willReturn(userInfo);

        // When & Then
        mvc.perform(get("/accounts/" + username))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("users/user-info"))
                .andExpect(model().attributeExists("userInfo"));

        then(userAccountQueryService).should().getUserAccountInfo(anyString(), anyString());
    }

    @WithUserDetails(value = "adminTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][view][GET] {username}의 회원정보 페이지 - 인증된 관리자 계정(ROLE_ADMIN)")
    @Test
    void givenUsernameWithAdminAccount_whenRequestingUserInfoView_thenReturnsUserInfoView() throws Exception {
        // Given
        var username = "userTest";
        var principalUsername = "adminTest";

        var userInfo = getUserInfo(1, username);

        given(userAccountQueryService.getUserAccountInfo(username, principalUsername)).willReturn(userInfo);

        // When & Then
        mvc.perform(get("/accounts/" + username))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("users/user-info"))
                .andExpect(model().attributeExists("userInfo"));

        then(userAccountQueryService).should().getUserAccountInfo(anyString(), anyString());
    }

    @WithUserDetails(value = "userTest2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[실패][view][GET] {username}의 회원정보 페이지 - {username}과 다른 인증된 사용자(ROLE_USER)")
    @Test
    void givenUsernameWithUnAuthorizedUserAccount_whenRequestingUserInfoView_thenRedirectsToLoginPage() throws Exception {
        // Given
        var username = "userTest";
        var principalUsername = "userTest2";

        given(userAccountQueryService.getUserAccountInfo(username, principalUsername))
                .willThrow(NoAuthorityToUpdateDeleteException.class);

        // When & Then
        mvc.perform(get("/accounts/" + username))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/error/denied"))
                .andReturn();

        then(userAccountQueryService).shouldHaveNoInteractions();
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][view][GET] {username}의 회원정보 수정 페이지 - 인증된 사용자 본인(ROLE_USER)")
    @Test
    void givenUsernameWithAuthorizedUserAccount_whenRequestingUserInfoEditView_thenReturnsUserInfoEditView() throws Exception {
        // Given
        var username = "userTest";
        var principalUsername = "userTest";

        var userInfo = getUserInfo(1, username);

        given(userAccountQueryService.getUserAccountInfo(username, principalUsername)).willReturn(userInfo);

        // When & Then
        mvc.perform(get("/accounts/" + username + "/edit"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("users/edit-form"))
                .andExpect(model().attributeExists("updateForm"));

        then(userAccountQueryService).should().getUserAccountInfo(anyString(), anyString());
    }

    @WithUserDetails(value = "userTest2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[실패][view][GET] {username}의 회원정보 수정 페이지 - {username}과 다른 인증된 사용자(ROLE_USER)")
    @Test
    void givenUsernameWithUnAuthorizedUserAccount_whenRequestingUserInfoEditView_thenRedirectsToErrorPage() throws Exception {
        // Given
        var username = "userTest";
        var principalUsername = "userTest2";

        given(userAccountQueryService.getUserAccountInfo(username, principalUsername))
                .willThrow(NoAuthorityToUpdateDeleteException.class);

        // When & Then
        mvc.perform(get("/accounts/" + username + "/edit"))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/error/denied"));

        then(userAccountQueryService).shouldHaveNoInteractions();
    }

    private static UserAccountInfo getUserInfo(int num, String username) {
        return UserAccountInfo.builder()
                .username(username)
                .userId((long) num)
                .email(username + "@gmail.com")
                .name("테스트유저" + num)
                .phoneNumber(String.format("010-%d%d%d%d-%d000", num, num, num, num, num))
                .role(UserAccount.RoleType.USER.getRoleValue())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }
}
