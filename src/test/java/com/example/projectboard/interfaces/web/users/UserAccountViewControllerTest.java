package com.example.projectboard.interfaces.web.users;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserAccountViewControllerTest {

    @Autowired
    private MockMvc mvc;

    @DisplayName("[성공][view][GET] 회원가입 페이지")
    @Test
    void givenNothing_whenRequestingSignUpView_thenReturnsSignUpView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/accounts/sign-up"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("users/sign-up"))
                .andDo(print());
    }

    @DisplayName("[성공][view][GET] 회원가입 성공 페이지")
    @Test
    void givenNothing_whenRequestingSignUpSuccessView_thenReturnsSignUpSuccessView() throws Exception {
        // Given
        // When & Then
        mvc.perform(get("/accounts/sign-up/success"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("users/sign-up-success"))
                .andDo(print());

    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][view][GET] Account me 페이지 - 인증된 사용자(ROLE_USER)")
    @Test
    void givenNothingWithRoleUserAccount_whenRequestingMeView_thenReturnsMeView() throws Exception {
        // Given
        // When & Then
        mvc.perform(get("/accounts/me"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("users/user-info"))
                .andExpect(model().attributeExists("userInfo"))
                .andDo(print());
    }

    @DisplayName("[실패][view][GET] Account me 페이지 - 인증되지 않은 사용자")
    @Test
    void givenNothingWithUnAuthenticatedUser_whenRequestingMeView_thenRedirectsToLoginView() throws Exception {
        // Given
        // When & Then
        mvc.perform(get("/accounts/me"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"))
                .andDo(print());
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][view][GET] 로그인된 회원정보 수정 페이지 - 인증된 사용자(ROLE_USER)")
    @Test
    void givenNothingWithRoleUserAccount_whenRequestingMeEditView_thenReturnsMeEditView() throws Exception {
        // Given
        // When & Then
        mvc.perform(get("/accounts/me/edit"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("users/edit-form"))
                .andExpect(model().attributeExists("updateForm"))
                .andDo(print());
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][view][GET] {username}의 회원정보 페이지 - 인증된 사용자 본인(ROLE_USER)")
    @Test
    void givenUsernameWithAuthorizedUserAccount_whenRequestingUserInfoView_thenReturnsUserInfoView() throws Exception {
        // Given
        var username = "userTest";
        // When & Then
        mvc.perform(get("/accounts/" + username))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("users/user-info"))
                .andExpect(model().attributeExists("userInfo"))
                .andDo(print());
    }

    @WithUserDetails(value = "adminTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][view][GET] {username}의 회원정보 페이지 - 인증된 관리자 계정(ROLE_ADMIN)")
    @Test
    void givenUsernameWithAdminAccount_whenRequestingUserInfoView_thenReturnsUserInfoView() throws Exception {
        // Given
        var username = "userTest";
        // When & Then
        mvc.perform(get("/accounts/" + username))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("users/user-info"))
                .andExpect(model().attributeExists("userInfo"))
                .andDo(print());
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[실패][view][GET] {username}의 회원정보 페이지 - {username}과 다른 인증된 사용자(ROLE_USER)")
    @Test
    void givenUsernameWithUnAuthorizedUserAccount_whenRequestingUserInfoView_thenRedirectsToLoginPage() throws Exception {
        // Given
        var username = "userTest2";
        // When & Then
        mvc.perform(get("/accounts/" + username))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/error/denied"))
                .andReturn();
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][view][GET] {username}의 회원정보 수정 페이지 - 인증된 사용자 본인(ROLE_USER)")
    @Test
    void givenUsernameWithAuthorizedUserAccount_whenRequestingUserInfoEditView_thenReturnsUserInfoEditView() throws Exception {
        // Given
        var username = "userTest";
        // When & Then
        mvc.perform(get("/accounts/" + username + "/edit"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("users/edit-form"))
                .andExpect(model().attributeExists("updateForm"))
                .andDo(print());
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[실패][view][GET] {username}의 회원정보 수정 페이지 - {username}과 다른 인증된 사용자(ROLE_USER)")
    @Test
    void givenUsernameWithUnAuthorizedUserAccount_whenRequestingUserInfoEditView_thenRedirectsToErrorPage() throws Exception {
        // Given
        var username = "userTest2";
        // When & Then
        mvc.perform(get("/accounts/" + username + "/edit"))
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/error/denied"))
                .andReturn();
    }
}
