package com.onpurple.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.onpurple.enums.ExpireEnum.*;

@Component
@RequiredArgsConstructor
public class RedisUtil {


    private final RedisTemplate<String, Object> redisTemplate;

    public void saveToken(String key, String value, long time) {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
    }

    public String getToken(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public boolean checkValidateToken(String key) {
        return redisTemplate.hasKey(key);
    }

    public void deleteToken(String key) {
        redisTemplate.delete(key);
    }
}
