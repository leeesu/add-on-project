package com.onpurple.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {


    private final RedisTemplate<String, Object> redisTemplate;

    public void saveData(String key, String value, long time) {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
    }

    public String getData(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public boolean checkValidateData(String key) {
        return redisTemplate.hasKey(key);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}
