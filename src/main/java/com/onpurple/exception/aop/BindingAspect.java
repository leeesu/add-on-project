package com.onpurple.exception.aop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
@Component
public class BindingAspect {
    // controller에 Bind되는 Service와, 매개변수 인수를 유효성 체크하기 위한 AOP
    private static final Logger log = LoggerFactory.getLogger(BindingAspect.class);

    @Around("execution(* com.onpurple.*..*Controller.*(..))")
    public Object validationBind(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 호출된 메서드의 클래스 이름과 메서드 이름 가져오기
        log.info("JointPoint");
        String type = proceedingJoinPoint.getSignature().getDeclaringTypeName();
        String method = proceedingJoinPoint.getSignature().getName();

        log.info("메서드 '{}'가 '{}'에서 호출되었습니다.", method, type);

        // 메서드에 전달된 파라미터 가져오기
        Object[] args = proceedingJoinPoint.getArgs();

        List<Map<String, String>> errorMaps = List.of(args)
                .stream()
                //arg가 BindResult의 인스턴스 조건에 맞는지 확인
                .filter(arg -> arg instanceof BindingResult)
                .map(arg -> (BindingResult) arg)
                .filter(BindingResult::hasErrors)
                .map(bindingResult -> {
                    Map<String, String> errorMap = new HashMap<>();
                    for (FieldError error : bindingResult.getFieldErrors()) {
                        log.warn("유효성 검사 오류: '{}'", error.getDefaultMessage());
                        errorMap.put(error.getField(), error.getDefaultMessage());
                    }
                    return errorMap;
                })
                .collect(Collectors.toList());

        if (!errorMaps.isEmpty()) {
            // 유효성 검사 오류가 있는 경우 여기에서 처리 한다.
            return errorMaps;
        }

        // 유효성 검사 오류가 없는 경우 메서드 실행을 허용
        return proceedingJoinPoint.proceed();
    }
}