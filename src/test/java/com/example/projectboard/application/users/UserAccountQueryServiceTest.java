package com.example.projectboard.application.users;

import com.example.projectboard.common.exception.NoAuthorityToReadException;
import com.example.projectboard.domain.users.UserAccount;
import com.example.projectboard.domain.users.UserAccountCommand;
import com.example.projectboard.domain.users.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class UserAccountQueryServiceTest {

    @Autowired
    private UserAccountQueryService sut;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][service] 사용자 계정 단건 조회(by userId) - 인증된 사용자 본인")
    @Test
    void givenUserIdWithAuthorizedUsername_whenGetUserAccountInfo_thenWorksFine() {
        // given
        var userId = 1L;
        var principalUsername = "userTest";

        // when
        var result = sut.getUserAccountInfo(userId, principalUsername);

        // then
        var findUser = userAccountRepository.findById(userId).orElse(null);
        assertThat(findUser).isNotNull();
        assertThat(result.getUsername()).isEqualTo(findUser.getUsername());
        assertThat(result.getUserId()).isEqualTo(findUser.getId());
    }

    @WithUserDetails(value = "adminTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][service] 사용자 계정 단건 조회(by userId) - 관리자 권한 계정")
    @Test
    void givenUserIdWithAdminRoleUsername_whenGetUserAccountInfo_thenWorksFine() {
        // given
        var userId = 1L;
        var principalUsername = "adminTest";

        // when
        var result = sut.getUserAccountInfo(userId, principalUsername);

        // then
        var findUser = userAccountRepository.findById(userId).orElse(null);
        assertThat(findUser).isNotNull();
        assertThat(result.getUsername()).isEqualTo(findUser.getUsername());
        assertThat(result.getUserId()).isEqualTo(findUser.getId());
    }

    @WithUserDetails(value = "userTest3", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[실패][service] 사용자 계정 단건 조회(by userId) - 조회 권한 없는 사용자")
    @Test
    void givenUserIdWithNoneAuthorizedUsername_whenGetUserAccountInfo_thenThrowsException() {
        // given
        var userId = 1L;
        var principalUsername = "userTest3";

        // when & then
        assertThatThrownBy(() -> sut.getUserAccountInfo(userId, principalUsername))
                .isInstanceOf(NoAuthorityToReadException.class);
    }

    @WithUserDetails(value = "userTest", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[성공][service] 사용자 계정 단건 조회(by username) - 인증된 사용자 본인")
    @Test
    void givenUsernameWithAuthorizedUsername_whenGetUserAccountInfo_thenWorksFine() {
        // given
        var username = "userTest";
        var principalUsername = "userTest";

        // when
        var result = sut.getUserAccountInfo(username, principalUsername);

        // then
        var findUser = userAccountRepository.findByUsername(username).orElse(null);
        assertThat(findUser).isNotNull();
        assertThat(result.getUsername()).isEqualTo(findUser.getUsername());
        assertThat(result.getUserId()).isEqualTo(findUser.getId());
    }

    @WithUserDetails(value = "userTest3", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[실패][service] 사용자 계정 단건 조회(by username) - 조회 권한 없는 사용자")
    @Test
    void givenUsernameWithNoneAuthorizedUsername_whenGetUserAccountInfo_thenThrowsException() {
        // given
        var username = "userTest";
        var principalUsername = "userTest3";

        // when & then
        assertThatThrownBy(() -> sut.getUserAccountInfo(username, principalUsername))
                .isInstanceOf(NoAuthorityToReadException.class);
    }

    @DisplayName("[성공][service] 검색 조건에 따른 사용자 리스트 페이징 조회 테스트 - 검색 by username")
    @Test
    void givenSearchConditionAndPageable_whenUserAccountsMethod_thenReturnsPageResult() {
        // given
        var condition = UserAccountCommand.SearchCondition
                .builder()
                .username("userTest")
                .build();

        var pageRequest = PageRequest.of(0, 5, Sort.Direction.DESC, "id");

        // when
        var result = sut.userAccounts(condition, pageRequest);

        // then
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getContent().get(0).getRole()).isEqualTo(UserAccount.RoleType.USER.getRoleValue());
    }

    @DisplayName("[성공][service] 전체 사용자 리스트 페이징 조회 테스트 - 검색 조건 없음")
    @Test
    void givenNullSearchConditionAndPageable_whenUserAccountsMethod_thenReturnsPageResult() {
        // given
        var condition = UserAccountCommand.SearchCondition.builder().build();
        var pageRequest = PageRequest.of(0, 5, Sort.Direction.DESC, "id");
        var total = userAccountRepository.count();

        // when
        var result = sut.userAccounts(condition, pageRequest);

        // then
        assertThat(result.getTotalElements()).isEqualTo(total);
        assertThat(result.getTotalPages())
                .isEqualTo((total / pageRequest.getPageSize()) + ((total % pageRequest.getPageSize() > 0) ? 1 : 0));
    }

    @DisplayName("[성공][service] 검색 조건에 따른 사용자 리스트 조회 테스트 - 검색 by username")
    @Test
    void givenSearchCondition_whenUserAccountListMethod_thenReturnsListResult() {
        // given
        var condition = UserAccountCommand.SearchCondition
                .builder()
                .username("admin")
                .build();

        // when
        var result = sut.userAccountList(condition);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getRole()).isEqualTo(UserAccount.RoleType.ADMIN.getRoleValue());
    }

    @DisplayName("[성공][service] 전체 사용자 리스트 조회 테스트 - 검색 조건 없음")
    @Test
    void givenNullSearchCondition_whenUserAccountListMethod_thenReturnsListResult() {
        // given
        var condition = UserAccountCommand.SearchCondition.builder().build();
        var total = userAccountRepository.count();

        // when
        var result = sut.userAccountList(condition);

        // then
        assertThat(result.size()).isEqualTo(total);
    }
}
