package com.onpurple.global.redis.cacheRepository;

import com.onpurple.global.enums.RedisKeyEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class CountCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void saveCount(String count) {
        String key = getKey();
        log.info("saveCount to Redis : {} : {}", key, count);
        redisTemplate.opsForValue().set(getKey(), count);
    }

    public Optional<String> getCount() {
        String key = getKey();
        String count = redisTemplate.opsForValue().get(key);
        log.info("getUser from Redis : {}", count);
        return Optional.ofNullable(count);
    }

    private String getKey() {
        return RedisKeyEnum.USER_COUNT_KEY.getDesc();
    }

}
