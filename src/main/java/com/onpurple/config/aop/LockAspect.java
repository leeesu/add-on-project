package com.onpurple.config.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // 분산 락 우선순위 설정
public class LockAspect {

    private final RedissonClient redissonClient;

    public LockAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Around("@annotation(DistributedLock)")
    public Object applyLock(ProceedingJoinPoint joinPoint) throws Throwable {
        String lockName = getLockName(joinPoint);
        RLock lock = redissonClient.getLock(lockName);

        try {
            lock.lock();
            return joinPoint.proceed();
        } finally {
            lock.unlock();
        }
    }

    private String getLockName(ProceedingJoinPoint joinPoint) {

        //메소드의 클래스명과 메소드명을 조합한 문자열로 락 이름 생성합니다.
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        return className + "_" + methodName;
    }
}