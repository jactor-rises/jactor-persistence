package com.gitlab.jactor.rises.facade.aop;

import com.gitlab.jactor.rises.commons.stack.StackResolver;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class StackLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(StackLogger.class);

    @AfterThrowing(pointcut = "execution(* com.gitlab.jactor..*(..))", throwing = "throwable")
    public void logStackOfException(JoinPoint joinPoint, Throwable throwable) {
        if (LOGGER.isErrorEnabled()) {
            LOGGER.error(String.format("exception in %s", joinPoint.getSignature().toShortString()));
            StackResolver.logStack(LOGGER::error, LOGGER::error, throwable);
        }
    }
}
