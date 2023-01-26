package com.example.projectboard.config;

import com.example.projectboard.security.PrincipalUserAccount;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisURI;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
@EnableRedisRepositories
@Configuration
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        var redisURI = RedisURI.create(redisProperties.getUrl());
        var configuration = LettuceConnectionFactory.createRedisConfiguration(redisURI);
        var connectionFactory = new LettuceConnectionFactory(configuration);
        connectionFactory.afterPropertiesSet();

        return connectionFactory;
    }

    @Bean
    public RedisTemplate<String, PrincipalUserAccount> principalUserAccountRedisTemplate(ObjectMapper objectMapper) {
        RedisTemplate<String, PrincipalUserAccount> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        var valueSerializer = new Jackson2JsonRedisSerializer<>(PrincipalUserAccount.class);
        valueSerializer.setObjectMapper(objectMapper);
        redisTemplate.setValueSerializer(valueSerializer);

        return redisTemplate;
    }
}
