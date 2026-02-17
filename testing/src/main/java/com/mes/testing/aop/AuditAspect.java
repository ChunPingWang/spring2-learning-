package com.mes.testing.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class AuditAspect {

    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT");

    @Before("execution(* com.mes.testing.adapter.in.web..*.create*(..)) || " +
            "execution(* com.mes.testing.adapter.in.web..*.update*(..)) || " +
            "execution(* com.mes.testing.adapter.in.web..*.delete*(..))")
    public void logAudit(JoinPoint joinPoint) {
        String method = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "anonymous";
        
        auditLog.info("AUDIT: User={} executed={}, args={}", username, method, Arrays.toString(args));
    }

    @AfterReturning(pointcut = "execution(* com.mes.testing.adapter.in.web..*.create*(..))", returning = "result")
    public void logCreateSuccess(JoinPoint joinPoint, Object result) {
        auditLog.info("AUDIT: Create operation succeeded, result={}", result);
    }
}
