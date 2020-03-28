package com.nemesiss.dev.oauthplayground.Exception;

import com.nemesiss.dev.oauthplayground.Model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
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
    public void handleThrowingException(Exception ex) { }
}
