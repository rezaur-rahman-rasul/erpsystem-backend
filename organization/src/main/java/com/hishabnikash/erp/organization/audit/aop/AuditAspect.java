package com.hishabnikash.erp.organization.audit.aop;

import com.hishabnikash.erp.organization.common.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AuditAspect {

    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        Signature signature = joinPoint.getSignature();
        String actorId = SecurityUtils.getCurrentUserIdOrSystem();
        long startedAt = System.nanoTime();

        try {
            log.info("audit.action={} actorId={} method={}.{} status=STARTED",
                    auditable.action(),
                    actorId,
                    signature.getDeclaringTypeName(),
                    signature.getName());

            Object result = joinPoint.proceed();

            log.info("audit.action={} actorId={} method={}.{} status=SUCCESS durationMs={}",
                    auditable.action(),
                    actorId,
                    signature.getDeclaringTypeName(),
                    signature.getName(),
                    elapsedMillis(startedAt));

            return result;
        } catch (Throwable ex) {
            log.warn("audit.action={} actorId={} method={}.{} status=FAILED durationMs={} error={}",
                    auditable.action(),
                    actorId,
                    signature.getDeclaringTypeName(),
                    signature.getName(),
                    elapsedMillis(startedAt),
                    ex.getClass().getSimpleName());
            throw ex;
        }
    }

    @Around("@annotation(com.hishabnikash.erp.organization.audit.aop.LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startedAt = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            Signature signature = joinPoint.getSignature();
            log.debug("execution.method={}.{} durationMs={}",
                    signature.getDeclaringTypeName(),
                    signature.getName(),
                    elapsedMillis(startedAt));
        }
    }

    private long elapsedMillis(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }
}
