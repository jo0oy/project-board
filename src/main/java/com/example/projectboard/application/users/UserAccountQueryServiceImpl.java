package com.example.projectboard.application.users;

import com.example.projectboard.common.exception.EntityNotFoundException;
import com.example.projectboard.common.exception.NoAuthorityToReadException;
import com.example.projectboard.common.exception.UsernameNotFoundException;
import com.example.projectboard.domain.users.UserAccount;
import com.example.projectboard.domain.users.UserAccountCommand;
import com.example.projectboard.domain.users.UserAccountInfo;
import com.example.projectboard.domain.users.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserAccountQueryServiceImpl implements UserAccountQueryService {

    private final UserAccountRepository userAccountRepository;

    /**
     * UserAccountInfo 를 단일 조회하는 메서드: 현재 로그인된 사용자 계정 조회하는 메서드
     * @param principalUsername : 현재 인증 객체(Security Context)에 담겨 있는 사용자 정보에서 추출한 username (String)
     *                          -> 현재 로그인된 사용자 계정 조회
     * @return UserAccountInfo : 조회한 UserAccount -> UserAccountInfo 객체로 반환.
     */
    @Override
    public UserAccountInfo getUserAccountInfo(String principalUsername) {
        log.info("{}: {}", getClass().getSimpleName(), "getUserAccountInfo(String)");

        // UserAccount 엔티티 조회
        var userAccount = userAccountRepository.findByUsername(principalUsername)
                .orElseThrow(UsernameNotFoundException::new);

        return UserAccountInfo.of(userAccount);
    }

    /**
     * UserAccountInfo 를 단일 조회하는 메서드.
     * @param id : 조회하고자 하는 UserAccount 의 id (Long)
     * @param principalUsername : 현재 인증 객체(Security Context)에 담겨 있는 사용자 정보에서 추출한 username (String)
     *                          -> 조회 권한 확인하기 위해
     * @return UserAccountInfo : 조회한 UserAccount -> UserAccountInfo 객체로 반환.
     */
    @Override
    public UserAccountInfo getUserAccountInfo(Long id, String principalUsername) {
        log.info("{}: {}", getClass().getSimpleName(), "getUserAccountInfo(Long, String)");

        // userAccount 엔티티 조회
        var userAccount = userAccountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다."));

        // principalUsername userAccount 엔티티 조회
        var principal = userAccountRepository.findByUsername(principalUsername)
                .orElseThrow(UsernameNotFoundException::new);

        // 개인 정보 조회 권한 확인
        if (!userAccount.equals(principal) && principal.getRole() != UserAccount.RoleType.ADMIN) {
            // 본인이 아니고 관리자 권한이 아닌 경우 예외 발생!
            log.error("개인정보 조회 권한이 없는 사용자입니다. username={}", principalUsername);
            throw new NoAuthorityToReadException("개인정보 조회 권한이 없는 사용자입니다.");
        }

        return UserAccountInfo.of(userAccount);
    }

    /**
     * UserAccountInfo 를 단일 조회하는 메서드.
     * @param username : 조회하고자 하는 UserAccount 의 username (String)
     * @param principalUsername : 현재 인증 객체(Security Context)에 담겨 있는 사용자 정보에서 추출한 username (String)
     *                          -> 조회 권한 확인하기 위해
     * @return UserAccountInfo : 조회한 UserAccount -> UserAccountInfo 객체로 반환.
     */
    @Override
    public UserAccountInfo getUserAccountInfo(String username, String principalUsername) {
        log.info("{}: {}", getClass().getSimpleName(), "getUserAccountInfo(String, String)");

        // userAccount 엔티티 조회
        var userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다."));

        // principalUsername UserAccount 엔티티 조회
        var principal = userAccountRepository.findByUsername(principalUsername)
                .orElseThrow(UsernameNotFoundException::new);

        // 개인 정보 조회 권한 확인
        if (!userAccount.equals(principal) && principal.getRole() != UserAccount.RoleType.ADMIN) {
            // 본인이 아니고 관리자 권한이 아닌 경우 예외 발생!
            log.error("개인정보 조회 권한이 없는 사용자입니다. username={}", principalUsername);
            throw new NoAuthorityToReadException("개인정보 조회 권한이 없는 사용자입니다.");
        }

        return UserAccountInfo.of(userAccount);
    }

    /**
     * 검색 조건(SearchCondition)에 따른 UserAccount 리스트 페이징 조회 메서드.
     * @param condition : UserAccount 검색 조건 (UserAccountCommand.SearchCondition)
     * @param pageable : 페이징을 위한 페이징 정보 (Pageable)
     * @return Page<UserAccountInfo> : 검색 조건에 부합하는 UserAccount 리스트의 페이징 결과를 UserAccountInfo Page 결과로 반환.
     */
    @Override
    public Page<UserAccountInfo> userAccounts(UserAccountCommand.SearchCondition condition, Pageable pageable) {
        log.info("{}: {}", getClass().getSimpleName(), "userAccounts(UserAccountCommand.SearchCondition, Pageable)");

        return userAccountRepository.findAllBySearchCondition(condition.toSearchCondition(), getPageRequest(pageable))
                .map(UserAccountInfo::of);
    }

    /**
     * 검색 조건(SearchCondition)에 따른 UserAccount 리스트 조회 메서드.
     * @param condition : UserAccount 검색 조건 (UserAccountCommand.SearchCondition)
     * @return List<UserAccountInfo> : 검색 조건에 부합하는 UserAccount 리스트 결과를 UserAccountInfo List 결과로 반환.
     */
    @Override
    public List<UserAccountInfo> userAccountList(UserAccountCommand.SearchCondition condition) {
        log.info("{}: {}", getClass().getSimpleName(), "userAccountList(UserAccountCommand.SearchCondition)");

        return userAccountRepository.findAllBySearchCondition(condition.toSearchCondition())
                .stream()
                .map(UserAccountInfo::of)
                .collect(Collectors.toList());
    }

    private PageRequest getPageRequest(Pageable pageable) {

        if(pageable.getPageNumber() < 0) {
            log.error("IllegalArgumentException. Invalid PageNumber!!");
            throw new IllegalArgumentException("페이지 번호는 0보다 작을 수 없습니다. 올바른 페이지 번호를 입력하세요.");
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
    }
}
