package com.example.projectboard.application.users;

import com.example.projectboard.common.exception.NoAuthorityToUpdateDeleteException;
import com.example.projectboard.common.exception.VerifyDuplicateException;
import com.example.projectboard.domain.users.UserAccount;
import com.example.projectboard.domain.users.UserAccountCommand;
import com.example.projectboard.domain.users.UserAccountInfo;
import com.example.projectboard.domain.users.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class UserAccountCommandServiceTest {

    @Autowired
    private UserAccountCommandService sut;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @DisplayName("[성공][service] 사용자 등록 테스트")
    @Test
    void givenRegisterReq_whenRegister_thenReturnsRegisteredUserAccountInfo() {
        //given
        var req = UserAccountCommand.RegisterReq
                .builder()
                .username("userTest4")
                .password("userTest4@!")
                .name("테스트유저4")
                .email("userTest4@naver.com")
                .phoneNumber("010-9999-9999")
                .role(UserAccount.RoleType.USER)
                .build();

        var beforeTotal = userAccountRepository.count();

        //when
        UserAccountInfo registeredUser = sut.registerUser(req);

        //then
        assertThat(userAccountRepository.count()).isEqualTo(beforeTotal + 1);
        assertThat(req.getUsername()).isEqualTo(registeredUser.getUsername());
    }

    @DisplayName("[실패][service] 사용자 등록 테스트 - 이미 존재하는 아이디")
    @Test
    void givenRegisterReqWithDuplicateUsername_whenRegister_thenReturnsVerifyDuplicateException() {
        //given
        var req = UserAccountCommand.RegisterReq
                .builder()
                .username("userTest")
                .password("userTest@pw!")
                .name("테스트유저01")
                .email("user100@naver.com")
                .phoneNumber("010-9090-9999")
                .role(UserAccount.RoleType.USER)
                .build();

        //when & then
        assertThatThrownBy(() -> sut.registerUser(req))
                .isNotNull()
                .isInstanceOf(VerifyDuplicateException.class);
    }

    @DisplayName("[실패][service] 사용자 등록 테스트 - 이미 존재하는 이메일")
    @Test
    void givenRegisterReqWithDuplicateEmail_whenRegister_thenReturnsVerifyDuplicateException() {
        //given
        var req = UserAccountCommand.RegisterReq
                .builder()
                .username("user9999")
                .password("user99991234")
                .name("유저9999")
                .email("userTest@gmail.com")
                .phoneNumber("010-9999-9999")
                .role(UserAccount.RoleType.USER)
                .build();

        //when & then
        assertThatThrownBy(() -> sut.registerUser(req))
                .isNotNull()
                .isInstanceOf(VerifyDuplicateException.class);
    }

    @DisplayName("[실패][service] 사용자 등록 테스트 - not null 필드에 null 값 전달")
    @Test
    void givenRegisterReqWithNullUsername_whenRegister_thenThrowsException() {
        //given
        var req = UserAccountCommand.RegisterReq
                .builder()
                .password("user991234")
                .name("유저99")
                .email("user99@naver.com")
                .phoneNumber("010-9999-9999")
                .role(UserAccount.RoleType.USER)
                .build();

        //when & then
        assertThatThrownBy(() -> sut.registerUser(req))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @WithUserDetails(value = "userTest3", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][service] 사용자 정보 수정 테스트")
    @Test
    void givenUserIdAndUpdateReq_whenUpdateUserInfo_thenWorksFine() {
        //given
        var userId = 3L;
        var principalUsername = "userTest3";
        var updateEmail = "usetTest3@naver.com";
        var req = UserAccountCommand.UpdateReq
                .builder()
                .email(updateEmail)
                .build();

        //when
        sut.updateUserInfo(userId, principalUsername, req);

        //then
        var updatedUser = userAccountRepository.findById(userId).orElse(null);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getEmail()).isEqualTo(updateEmail);
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[실패][service] 사용자 정보 수정 테스트 - 수정 권한 없는 사용자")
    @Test
    void givenUserIdUpdateReqWithForbiddenUsername_whenUpdateUserInfo_thenThrowsException() {
        //given
        var userId = 3L;
        var principalUsername = "userTest";
        var updateEmail = "user3@naver.com";
        var req = UserAccountCommand.UpdateReq
                .builder()
                .email(updateEmail)
                .build();

        //when & then
        assertThatThrownBy(() -> sut.updateUserInfo(userId, principalUsername, req))
                .isInstanceOf(NoAuthorityToUpdateDeleteException.class);
    }

    @WithUserDetails(value = "adminTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][service] 사용자 삭제 테스트 - 관리자 권한 사용자가 일반 사용자 삭제")
    @Test
    void givenUserAccountId_whenDelete_thenWorksFine() {
        //given
        var userId = 1L;
        var principalUsername = "adminTest";
        var beforeDelete = userAccountRepository.count();

        //when
        sut.delete(userId, principalUsername);

        //then
        assertThat(userAccountRepository.count()).isEqualTo(beforeDelete - 1);
    }

    @WithUserDetails(value = "userTest3", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[실패][service] 사용자 삭제 테스트 - 삭제 권한 없는 사용자")
    @Test
    void givenUserIdWithForbiddenUsername_whenDelete_thenThrowsException() {
        //given
        var userId = 2L;
        var principalUsername = "userTest3";

        //when & then
        assertThatThrownBy(() -> sut.delete(userId, principalUsername))
                .isInstanceOf(NoAuthorityToUpdateDeleteException.class);
    }
}
