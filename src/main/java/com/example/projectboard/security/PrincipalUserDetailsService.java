package com.example.projectboard.security;

import com.example.projectboard.domain.users.UserAccountCacheRepository;
import com.example.projectboard.domain.users.UserAccountInfoMapper;
import com.example.projectboard.domain.users.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j
@RequiredArgsConstructor
public class PrincipalUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;
    private final UserAccountCacheRepository redisRepository;
    private final UserAccountInfoMapper userAccountInfoMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("{}: {}", getClass().getSimpleName(), "loadUserByUsername(String)");

        var data = redisRepository.get(username); // redis 에서 get

        return data.map(PrincipalUserAccount::new)
                .orElseGet(() -> {
                    var user =  userAccountRepository.findByUsername(username)
                            .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다. username=" + username));

                    redisRepository.set(user); // redis 에 저장

                    return new PrincipalUserAccount(user);
                });
    }
}
