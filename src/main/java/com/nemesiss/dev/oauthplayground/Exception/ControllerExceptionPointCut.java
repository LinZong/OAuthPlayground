package com.nemesiss.dev.oauthplayground.Exception;

import com.nemesiss.dev.oauthplayground.Model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ControllerExceptionPointCut {

    @Pointcut("execution(public * com.nemesiss.dev.oauthplayground.Controller.*.*(..))")
    public void handleException() { }

    @AfterThrowing(pointcut = "handleException()", throwing = "ex")
    public ErrorResponse handleThrowingException(Exception ex) {
        return AOPExceptionHandler.exceptionGet(ex);
    }

    @Around(value = "handleException()")
    public Object process(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = null;
        try {
            result = proceedingJoinPoint.proceed();
        } catch (Exception e) {
            return AOPExceptionHandler.exceptionGet(e);
        }
        return result;
    }
}
