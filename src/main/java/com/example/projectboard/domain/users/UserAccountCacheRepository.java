package com.example.projectboard.domain.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UserAccountCacheRepository {

    private final RedisTemplate<String, UserAccountCacheDto> userRedisTemplate;
    private final UserAccountInfoMapper userAccountInfoMapper;

    private static final Duration USER_ACCOUNT_TTL = Duration.ofDays(3);

    public void set(UserAccount userAccount) {
        var key = getKey(userAccount.getUsername());
        var data = userAccountInfoMapper.toCacheDto(userAccount);

        log.info("Set User to Redis: key={}, value={}", key, data);

        userRedisTemplate.opsForValue().set(key, data, USER_ACCOUNT_TTL);
    }

    public Optional<UserAccountCacheDto> get(String username) {
        var data = userRedisTemplate.opsForValue().get(getKey(username));

        log.info("Get User from Redis: data={}", data);

        return Optional.ofNullable(data);
    }

    public void delete(String username) {
        var key = getKey(username);
        log.info("Delete User from Redis: key={}", key);

        userRedisTemplate.delete(key);
    }

    private String getKey(String username) {
        return "UID:" + username;
    }
}
