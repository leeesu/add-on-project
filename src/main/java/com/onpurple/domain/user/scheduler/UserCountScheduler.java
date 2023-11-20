package com.onpurple.domain.user.scheduler;

import com.onpurple.domain.user.repository.UserRepository;
import com.onpurple.global.redis.cacheRepository.CountCacheRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserCountScheduler {
    private final CountCacheRepository countCacheRepository;
    private final UserRepository userRepository;

    public UserCountScheduler(CountCacheRepository countCacheRepository, UserRepository userRepository) {
        this.countCacheRepository = countCacheRepository;
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void resetUserCount() {
        Long userCountDB = userRepository.count(); // 회원수 조회
        countCacheRepository.saveCount(String.valueOf(userCountDB)); // 회원 수 업데이트
    }
}
