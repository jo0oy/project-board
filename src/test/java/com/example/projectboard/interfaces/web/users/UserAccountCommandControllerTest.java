package com.example.projectboard.interfaces.web.users;

import com.example.projectboard.application.users.UserAccountCommandService;
import com.example.projectboard.common.exception.NoAuthorityToUpdateDeleteException;
import com.example.projectboard.config.TestSecurityConfig;
import com.example.projectboard.domain.users.UserAccount;
import com.example.projectboard.domain.users.UserAccountCommand;
import com.example.projectboard.domain.users.UserAccountInfo;
import com.example.projectboard.interfaces.dto.users.UserAccountDto;
import com.example.projectboard.interfaces.dto.users.UserAccountDtoMapper;
import com.example.projectboard.interfaces.dto.users.UserAccountDtoMapperImpl;
import com.example.projectboard.util.FormDataEncoder;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({FormDataEncoder.class, TestSecurityConfig.class})
@ComponentScan(basePackageClasses = {UserAccountDtoMapper.class, UserAccountDtoMapperImpl.class})
@AutoConfigureMockMvc
@WebMvcTest(controllers = UserAccountCommandController.class)
public class UserAccountCommandControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private FormDataEncoder encoder;

    @SpyBean
    private UserAccountDtoMapper userAccountDtoMapper;

    @MockBean
    private UserAccountCommandService userAccountCommandService;

    @DisplayName("[성공][controller][POST] 유저 등록 테스트")
    @Test
    void givenRegisterReq_WhenPostMapping_ThenRedirectToSignUpSuccessPage() throws Exception {
        //given
        var username = "newUser";
        var password = "newUser1234!";
        var name = "새로운유저";
        var email = "newUser@naver.com";
        var phoneNumber = "010-9090-0909";
        var role = UserAccount.RoleType.USER;
        var registerForm = getRegisterForm(username, password, name, email, phoneNumber, role);

        given(userAccountCommandService.verifyDuplicateUsername(username)).willReturn(true);
        given(userAccountCommandService.verifyDuplicateEmail(email)).willReturn(true);
        given(userAccountCommandService.registerUser(any(UserAccountCommand.RegisterReq.class)))
                .willReturn(any(UserAccountInfo.class));

        //when
        mvc.perform(post("/accounts/sign-up")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(registerForm))
                        .with(csrf())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/**/sign-up/success"))
                .andDo(print());

        then(userAccountCommandService).should().verifyDuplicateUsername(eq(username));
        then(userAccountCommandService).should().verifyDuplicateEmail(eq(email));
        then(userAccountCommandService).should().registerUser(any(UserAccountCommand.RegisterReq.class));
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][controller][PUT] 유저 정보 수정 테스트 - 인증된 사용자(본인)")
    @Test
    void givenUpdateReq_WhenPutMapping_ThenRedirectToMePage() throws Exception {

        //given
        var userId = 1L;
        var username = "userTest";
        var email = "userTest@gmail.com";
        var updateEmail = "userTest@email.com";
        var updatePhoneNumber = "010-0101-1010";

        var updateReq = getUpdateForm(userId, email, updateEmail, updatePhoneNumber);

        given(userAccountCommandService.verifyDuplicateEmail(updateEmail)).willReturn(true);
        willDoNothing().given(userAccountCommandService)
                .updateUserInfo(eq(userId), eq(username), any(UserAccountCommand.UpdateReq.class));

        //when & then
        mvc.perform(put("/accounts/" + userId + "/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(updateReq))
                        .with(csrf())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/**/me"));

        then(userAccountCommandService).should().verifyDuplicateEmail(eq(updateEmail));
        then(userAccountCommandService).should()
                .updateUserInfo(anyLong(), anyString(), any(UserAccountCommand.UpdateReq.class));
    }

    @DisplayName("[실패][controller][PUT] 유저 정보 수정 테스트 - 인증되지 않은 사용자")
    @Test
    void givenUpdateReqAndNotAuthenticated_WhenPutMapping_ThenRedirectToLoginPage() throws Exception {

        //given
        var userId = 1L;
        var email = "userTest@gmail.com";
        var updateEmail = "userTest@email.com";
        var updatePhoneNumber = "010-0101-1010";

        var updateReq = getUpdateForm(userId, email, updateEmail, updatePhoneNumber);

        given(userAccountCommandService.verifyDuplicateEmail(updateEmail)).willReturn(true);
        willDoNothing().given(userAccountCommandService)
                .updateUserInfo(eq(userId), anyString(), any(UserAccountCommand.UpdateReq.class));

        //when & then
        mvc.perform(put("/accounts/" + userId + "/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(updateReq))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        then(userAccountCommandService).shouldHaveNoInteractions();
    }

    @WithUserDetails(value = "userTest2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[실패][controller][PUT] 유저 정보 수정 테스트 - 수정 권한이 없는 인증된 사용자")
    @Test
    void givenUpdateReqWithForbiddenUsername_WhenPutMapping_ThenThrowsException() throws Exception {

        //given
        var userId = 1L;
        var email = "userTest@gmail.com";
        var updateEmail = "userTest@email.com";
        var updatePhoneNumber = "010-0101-1010";

        var updateReq = getUpdateForm(userId, email, updateEmail, updatePhoneNumber);

        given(userAccountCommandService.verifyDuplicateEmail(updateEmail)).willReturn(true);
        willThrow(NoAuthorityToUpdateDeleteException.class)
                .given(userAccountCommandService).updateUserInfo(eq(userId), anyString(), any(UserAccountCommand.UpdateReq.class));

        //when & then
        var mvcResult = mvc.perform(put("/accounts/" + userId + "/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(encoder.encode(updateReq))
                        .with(csrf()))
                .andExpect(status().is4xxClientError()).andReturn();

        then(userAccountCommandService).should().verifyDuplicateEmail(eq(updateEmail));
        then(userAccountCommandService).should()
                .updateUserInfo(anyLong(), anyString(), any(UserAccountCommand.UpdateReq.class));
        assertThat(mvcResult.getResolvedException()).isNotNull();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(NoAuthorityToUpdateDeleteException.class);
    }

    @WithUserDetails(value = "adminTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][controller][DELETE] 유저 삭제 테스트 - 인증된 관리자 계정")
    @Test
    void givenUserIdWithAdminUser_WhenDeleteMapping_ThenRedirectToMainPage() throws Exception {

        //given
        var userId = 3L;

        willDoNothing().given(userAccountCommandService).delete(eq(userId), anyString());

        //when & then
        mvc.perform(delete("/accounts/" + userId)
                        .with(csrf())
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        then(userAccountCommandService).should().delete(eq(userId), anyString());
    }

    @WithUserDetails(value = "userTest2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[실패][controller][DELETE] 유저 삭제 테스트 - 삭제 권한이 없는 인증된 사용자")
    @Test
    void givenUserIdWithForbiddenUsername_WhenDeleteMapping_ThenThrowsException() throws Exception {

        //given
        var userId = 2L;

        willThrow(NoAuthorityToUpdateDeleteException.class).willDoNothing()
                .given(userAccountCommandService).delete(eq(userId), anyString());

        //when & then
        var mvcResult = mvc.perform(delete("/accounts/" + userId).with(csrf()))
                .andExpect(status().is4xxClientError()).andReturn();

        then(userAccountCommandService).should().delete(eq(userId), anyString());
        assertThat(mvcResult.getResolvedException()).isNotNull();
        assertThat(mvcResult.getResolvedException()).isInstanceOf(NoAuthorityToUpdateDeleteException.class);
    }

    private static UserAccountDto.RegisterForm getRegisterForm(String username,
                                                               String password,
                                                               String name,
                                                               String email,
                                                               String phoneNumber,
                                                               UserAccount.RoleType role) {
        return UserAccountDto.RegisterForm.builder()
                .username(username)
                .password(password)
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .role(role)
                .build();
    }

    private static UserAccountDto.UpdateForm getUpdateForm(Long userId,
                                                           String email,
                                                           String updateEmail,
                                                           String updatePhoneNumber) {
        return UserAccountDto.UpdateForm.builder()
                .userId(userId)
                .beforeEmail(email)
                .email(updateEmail)
                .phoneNumber(updatePhoneNumber)
                .build();
    }
}
