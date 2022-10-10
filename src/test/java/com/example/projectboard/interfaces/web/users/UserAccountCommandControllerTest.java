package com.example.projectboard.interfaces.web.users;

import com.example.projectboard.common.exception.NoAuthorityToUpdateDeleteException;
import com.example.projectboard.domain.users.UserAccount;
import com.example.projectboard.domain.users.UserAccountRepository;
import com.example.projectboard.interfaces.dto.users.UserAccountDto;
import com.example.projectboard.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(FormDataEncoder.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserAccountCommandControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private FormDataEncoder encoder;

    @DisplayName("[성공][controller][POST] 유저 등록 테스트")
    @Test
    void givenRegisterReq_WhenPostMapping_ThenRedirectToMainPage() throws Exception {

        //given
        var username = "newUser";
        var password = "newUser1234";
        var name = "새로운유저";
        var email = "newUser@naver.com";
        var phoneNumber = "010-9090-0909";
        var role = UserAccount.RoleType.USER;
        var registerReq = UserAccountDto.RegisterReq.builder()
                .username(username)
                .password(password)
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .role(role)
                .build();

        var beforeRegister = userAccountRepository.count();

        //when & then
        mvc.perform(post("/user-accounts")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(registerReq))
                        .with(csrf())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/**/sign-up/success"));

        assertThat(userAccountRepository.count()).isEqualTo(beforeRegister + 1);
    }

    @WithUserDetails(value = "jo0oy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][controller][PUT] 유저 정보 수정 테스트 - 인증된 사용자(본인)")
    @Test
    void givenUserIdAndUpdateReq_WhenPutMapping_ThenRedirectToMePage() throws Exception {

        //given
        var userId = 1L;
        var updateEmail = "jo0oy@naver.com";
        var updatePhoneNumber = "010-0101-1010";

        var updateReq = UserAccountDto.UpdateReq.builder()
                .email(updateEmail)
                .phoneNumber(updatePhoneNumber)
                .build();

        //when & then
        mvc.perform(put("/user-accounts/" + userId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(updateReq))
                        .with(csrf())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/**/me"));

        var updatedUser = userAccountRepository.findById(userId).orElse(null);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getEmail()).isEqualTo(updateEmail);
        assertThat(updatedUser.getPhoneNumber()).isEqualTo(updatePhoneNumber);
    }

    @WithUserDetails(value = "yooj", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[실패][controller][PUT] 유저 정보 수정 테스트 - 수정 권한이 없는 인증된 사용자")
    @Test
    void givenUserIdAndUpdateReqWithForbiddenUsername_WhenPutMapping_ThenThrowsException() throws Exception {

        //given
        var userId = 1L;
        var updateEmail = "jo0oy@naver.com";
        var updatePhoneNumber = "010-0101-1010";

        var updateReq = UserAccountDto.UpdateReq.builder()
                .email(updateEmail)
                .phoneNumber(updatePhoneNumber)
                .build();

        //when & then
        var mvcResult = mvc.perform(delete("/user-accounts/" + userId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(updateReq))
                        .with(csrf()))
                .andExpect(status().is4xxClientError()).andReturn();

        assertThat(mvcResult.getResolvedException()).isNotNull();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(NoAuthorityToUpdateDeleteException.class);
    }

    @WithUserDetails(value = "admin1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][controller][DELETE] 유저 삭제 테스트 - 인증된 관리자 계정")
    @Test
    void givenUserId_WhenDeleteMapping_ThenRedirectToMainPage() throws Exception {

        //given
        var userId = 3L;
        var beforeDelete = userAccountRepository.count();

        //when & then
        mvc.perform(delete("/user-accounts/" + userId)
                        .with(csrf())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        assertThat(userAccountRepository.count()).isEqualTo(beforeDelete - 1);
    }

    @WithUserDetails(value = "jo0oy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[실패][controller][DELETE] 유저 삭제 테스트 - 삭제 권한이 없는 인증된 사용자")
    @Test
    void givenUserIdWithForbiddenUsername_WhenDeleteMapping_ThenThrowsException() throws Exception {

        //given
        var userId = 2L;

        //when & then
        var mvcResult = mvc.perform(delete("/user-accounts/" + userId).with(csrf()))
                .andExpect(status().is4xxClientError()).andReturn();

        assertThat(mvcResult.getResolvedException()).isNotNull();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(NoAuthorityToUpdateDeleteException.class);
    }

}