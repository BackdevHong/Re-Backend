package org.honginsung.aop.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.honginsung.aop.dto.User;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Aspect
@Component
public class DecodeAop {
    @Pointcut("execution(* org.honginsung.aop.controller..*.*(..))")
    private void cut() {}


    @Pointcut("@annotation(org.honginsung.aop.annotation.Decode)")
    private void enableDecode() {}


    @Before("cut() && enableDecode()")
    public void before(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            if (arg instanceof User user) {
                String base64Email = user.getEmail();
                String email = new String(Base64.getDecoder().decode(base64Email), StandardCharsets.UTF_8);
                user.setEmail(email);
            }
        }
    }

    @AfterReturning(value = "cut() && enableDecode()", returning = "returnObj")
    public void afterReturn(JoinPoint joinPoint, Object returnObj) {
        if (returnObj instanceof User user) {
            String base64Email = user.getEmail();
            String email = Base64.getEncoder().encodeToString(base64Email.getBytes());
            user.setEmail(email);
        }
    }
}
