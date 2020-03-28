package com.nemesiss.dev.oauthplayground.PointCut;

import com.nemesiss.dev.oauthplayground.Exception.PlaygroundNotExistedException;
import com.nemesiss.dev.oauthplayground.ParamValidator.PlaygroundIDValidator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class PlaygroundIDVerifyPointCut {

    @Autowired
    StringRedisTemplate stringRedis;

    @Pointcut("@annotation(com.nemesiss.dev.oauthplayground.ParamValidator.PlaygroundIDValidator)")
    public void verifyPlaygroundID() {
    }

    @Around("verifyPlaygroundID()")
    public Object doVerifyPlaygroundID(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        PlaygroundIDValidator validator = method.getAnnotation(PlaygroundIDValidator.class);
        Object[] args = proceedingJoinPoint.getArgs();

        Object result = null;
        if (args.length > validator.order() && args[validator.order()] instanceof String) {
            String playgroundID = (String) args[validator.order()];
            String content = stringRedis.opsForValue().get(playgroundID);
            if (content == null) {
                throw new PlaygroundNotExistedException(1001, playgroundID);
            }
        }
        result = proceedingJoinPoint.proceed();
        return result;
    }
}
