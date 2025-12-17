package com.juwon.springcommunity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        
        // Key는 String으로 직렬화
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        
        // Value는 다양한 타입을 저장할 수 있으나, 여기서는 String으로 기본 설정합니다.
        // 만약 객체를 저장하려면 Jackson2JsonRedisSerializer 등을 사용해야 합니다.
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        
        // Hash Key와 Value에 대한 직렬화 설정 (필요시)
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        
        return redisTemplate;
    }
}
