package com.example.projectboard.application.users;

import com.example.projectboard.common.exception.EntityNotFoundException;
import com.example.projectboard.common.exception.NoAuthorityToUpdateDeleteException;
import com.example.projectboard.common.exception.UsernameNotFoundException;
import com.example.projectboard.common.exception.VerifyDuplicateException;
import com.example.projectboard.domain.users.UserAccount;
import com.example.projectboard.domain.users.UserAccountCommand;
import com.example.projectboard.domain.users.UserAccountInfo;
import com.example.projectboard.domain.users.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAccountCommandServiceImpl implements UserAccountCommandService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 새로운 UserAccount 등록 메서드.
     * @param command : 새로운 사용자 등록을 위한 데이터를 담은 객체 (UserAccountCommand.RegisterReq)
     * @return UserAccountInfo : 등록된 UserAccount 객체를 UserAccountInfo 에 mapping 해 반환.
     */
    @Override
    @Transactional
    public UserAccountInfo registerUser(UserAccountCommand.RegisterReq command) {
        log.info("{}: {}", getClass().getSimpleName(), "registerUser(UserAccountCommand.RegisterReq)");

        // 중복 아이디/이메일 확인
        verifyDuplicateUsernameEmail(command.getUsername(), command.getEmail());

        var userAccount = command.toEntity();

        // 인코딩된 패스워드로 update
        userAccount.updatePassword(passwordEncoder.encode(command.getPassword()));

        return UserAccountInfo.of(userAccountRepository.save(userAccount));
    }

    /**
     * 사용자(UserAccount) 정보 수정 메서드.
     * @param userAccountId : 수정하려는 UserAccount id (Long)
     * @param principalUsername : 현재 인증 객체(Security Context)에 담겨 있는 사용자의 username (String) -> 수정 권한 확인 위해
     * @param command : UserAccount 수정 내용 담은 객체 (UserAccountCommand.UpdateReq)
     */
    @Override
    @Transactional
    public void updateUserInfo(Long userAccountId, String principalUsername, UserAccountCommand.UpdateReq command) {
        log.info("{}: {}", getClass().getSimpleName(), "updateUserInfo(Long, String, UserAccountCommand.UpdateReq)");

        // 수정할 엔티티 조회
        var userAccount = userAccountRepository.findById(userAccountId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다."));

        // 현재 로그인된 사용자 엔티티 조회
        var principal = userAccountRepository.findByUsername(principalUsername)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다. username=" + principalUsername));

        // 수정 권한 확인
        if (userAccount.equals(principal) || principal.getRole() == UserAccount.RoleType.ADMIN) {
            // 유저 정보 업데이트
            userAccount.updateUserInfo(command.getEmail(), command.getPhoneNumber());
        } else {
            log.error("수정 권한이 없는 사용자입니다. username={}", principalUsername);
            throw new NoAuthorityToUpdateDeleteException();
        }
    }

    /**
     * 사용자(UserAccount) 계정 삭제 메서드.
     * @param userAccountId : 삭제하려는 UserAccount id (Long)
     * @param principalUsername : 현재 인증 객체(Security Context)에 담겨 있는 사용자의 username (String) -> 삭제 권한 확인 위해
     */
    @Override
    @Transactional
    public void delete(Long userAccountId, String principalUsername) {
        log.info("{}: {}", getClass().getSimpleName(), "delete(Long, String)");

        // 삭제할 엔티티 조회
        var userAccount = userAccountRepository.findById(userAccountId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다."));

        // 현재 로그인된 사용자 엔티티 조회
        var principal = userAccountRepository.findByUsername(principalUsername)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다. username=" + principalUsername));

        // 삭제 권한 확인
        if (userAccount.equals(principal) || principal.getRole() == UserAccount.RoleType.ADMIN) {
            // 유저 엔티티 삭제
            userAccountRepository.delete(userAccount);
        } else {
            log.error("삭제 권한이 없는 사용자입니다. username={}", principalUsername);
            throw new NoAuthorityToUpdateDeleteException();
        }
    }

    @Override
    public boolean verifyDuplicateUsername(String username) {
        return userAccountRepository.findByUsername(username).isEmpty();
    }

    @Override
    public boolean verifyDuplicateEmail(String email) {
        return userAccountRepository.findByEmail(email).isEmpty();
    }

    private void verifyDuplicateUsernameEmail(String username, String email) {
        log.info("아이디 & 이메일 중복 검증 로직 실행");
        if (userAccountRepository.existsByUsername(username)) {
            log.error("이미 존재하는 아이디입니다. username={}", username);
            throw new VerifyDuplicateException("이미 존재하는 아이디입니다. username=" + username);
        }

        if (userAccountRepository.existsByEmail(email)) {
            log.error("이미 사용중인 이메일입니다. email={}", email);
            throw new VerifyDuplicateException("이미 사용중인 이메일입니다. email=" + email);
        }
    }
}
