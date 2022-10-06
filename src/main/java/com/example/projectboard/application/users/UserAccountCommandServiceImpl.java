package com.example.projectboard.application.users;

import com.example.projectboard.common.exception.EntityNotFoundException;
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

    @Override
    @Transactional
    public UserAccountInfo registerUser(UserAccountCommand.RegisterReq command) {
        log.info("{}: {}", getClass().getSimpleName(), "registerUser(UserAccountCommand.RegisterReq)");

        var userAccount = command.toEntity();

        return UserAccountInfo.of(userAccountRepository.save(userAccount));
    }

    @Override
    @Transactional
    public void updateUserInfo(Long userAccountId, UserAccountCommand.UpdateReq command) {
        log.info("{}: {}", getClass().getSimpleName(), "updateUserInfo(Long, UserAccountCommand.UpdateReq)");

        // 수정할 엔티티 조회
        var userAccount = userAccountRepository.findById(userAccountId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다."));

        // 유저 정보 업데이트
        userAccount.updateUserInfo(command.getEmail(), command.getPhoneNumber());
    }

    @Override
    @Transactional
    public void delete(Long userAccountId) {
        log.info("{}: {}", getClass().getSimpleName(), "delete(Long)");

        // 삭제할 엔티티 조회
        var userAccount = userAccountRepository.findById(userAccountId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다."));

        // 엔티티 삭제
        userAccountRepository.delete(userAccount);
    }
}
