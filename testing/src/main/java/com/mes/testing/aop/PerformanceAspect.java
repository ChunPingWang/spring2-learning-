package com.mes.testing.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class PerformanceAspect {

    private static final Logger log = LoggerFactory.getLogger(PerformanceAspect.class);

    @Pointcut("execution(* com.mes.testing.application..*.*(..))")
    public void applicationLayer() {}

    @Pointcut("@annotation(PerfMonitor)")
    public void perfMonitored() {}

    @Around("applicationLayer() || perfMonitored()")
    public Object measureTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = pjp.getSignature().toShortString();
        
        try {
            Object result = pjp.proceed();
            long duration = System.currentTimeMillis() - start;
            log.info("方法 {} 執行時間: {}ms", methodName, duration);
            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - start;
            log.error("方法 {} 執行失敗，耗時: {}ms, 異常: {}", methodName, duration, ex.getMessage());
            throw ex;
        }
    }

    @Around("execution(* com.mes.testing.adapter.in.web..*.*(..))")
    public Object logRequest(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().toShortString();
        Object[] args = pjp.getArgs();
        
        log.debug("請求進入: {}, 參數: {}", methodName, Arrays.toString(args));
        
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long duration = System.currentTimeMillis() - start;
        
        log.debug("請求完成: {}, 耗時: {}ms", methodName, duration);
        return result;
    }
}
