package com.onpurple.global.redis.cacheRepository;

import com.onpurple.domain.user.model.User;
import com.onpurple.global.enums.RedisKeyEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UserCacheRepository {
    private final RedisTemplate<String, User> redisTemplate;
    private final Duration USER_CACHE_EXPIRE_TIME = Duration.ofDays(5);

    public void saveUser(User user) {
        String key = getKey(user.getUsername());
        log.info("saveUser to Redis : {} : {}", key,user);
        redisTemplate.opsForValue().set(getKey(user.getUsername()), user, USER_CACHE_EXPIRE_TIME);
    }

    public Optional<User> getUser(String username) {
        String key = getKey(username);
        User user = redisTemplate.opsForValue().get(key);
        log.info("getUser from Redis : {} ({})", username, user);
        return Optional.ofNullable(user);
    }

    private String getKey(String username) {
        return RedisKeyEnum.LOGIN_USER_KEY.getDesc() + username;
    }


}


