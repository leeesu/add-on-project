package com.onpurple.external;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisUtil {


    private static final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

    public static void saveData(String key, String value, long time) {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, time, TimeUnit.MILLISECONDS);
    }

    public static String getData(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public static boolean checkValidateData(String key) {
        return redisTemplate.hasKey(key);
    }

    public static void deleteData(String key) {
        redisTemplate.delete(key);
    }
}
