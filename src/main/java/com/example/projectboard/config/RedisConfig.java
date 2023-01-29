package com.example.projectboard.config;

import com.example.projectboard.domain.users.UserAccountCacheDto;
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
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@EnableRedisRepositories
@Configuration
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        var url = redisProperties.getUrl();
        var redisURI = (StringUtils.hasText(url)) ? RedisURI.create(url)
                : RedisURI.builder().withHost(redisProperties.getHost()).withPort(redisProperties.getPort()).build();

        var config = LettuceConnectionFactory.createRedisConfiguration(redisURI);
        var factory = new LettuceConnectionFactory(config);
        factory.afterPropertiesSet();

        return factory;
    }

    @Bean
    public RedisTemplate<String, UserAccountCacheDto> userRedisTemplate(ObjectMapper objectMapper) {
        var redisTemplate = new RedisTemplate<String, UserAccountCacheDto>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        redisTemplate.setKeySerializer(new StringRedisSerializer());

        var serializer = new Jackson2JsonRedisSerializer<>(UserAccountCacheDto.class);
        serializer.setObjectMapper(objectMapper);
        redisTemplate.setValueSerializer(serializer);

        return redisTemplate;
    }
}
