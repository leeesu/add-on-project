package com.onpurple.config;

import com.onpurple.dto.response.ChatMessageDto;
import com.onpurple.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisHost);
        redisStandaloneConfiguration.setPort(redisPort);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }
    @Bean
    public RedisMessageListenerContainer redisMessageListener(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        return container;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory, Object.class);
    }

    @Bean
    public RedisTemplate<String, ChatMessageDto> redisTemplateMessage(RedisConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory, ChatMessageDto.class);
    }

    @Bean
    public RedisTemplate<String, User> redisTemplateUser(RedisConnectionFactory connectionFactory) {
        return createRedisTemplate(connectionFactory, User.class);
    }

    private <T> RedisTemplate<String, T> createRedisTemplate(RedisConnectionFactory connectionFactory,
                                                             Class<T> valueType) {
        RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        /** Jackson2JsonRedisSerializer를 사용하여 Java 객체를 JSON으로 직렬화
         * valueType 매개변수로 제네릭 타입을 동적으로 설정
         */
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(valueType));

        return redisTemplate;
    }
}
