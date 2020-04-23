package com.nemesiss.dev.oauthplayground.PointCut;

import com.nemesiss.dev.oauthplayground.Utils.PlaygroundUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;

@Aspect
@Component
@Slf4j
public class DebugRedirectPointCut {


    @Value("${debug.redirect}")
    boolean ShouldRedirect;

    @Pointcut("@annotation(com.nemesiss.dev.oauthplayground.Annotations.DebugRedirect)")
    public void handleReturnOrRedirect() {
    }

    @Around("handleReturnOrRedirect()")
    public Object doHandleReturnOrRedirect(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        /*
            对于每个请求, DispatcherServlet会把当前的HTTPServletRequest and HTTPServletResponse绑定在RequestContextHolder的
            一个static ThreadLocal里，因此当AOP函数和请求处理函数在同一条线程里的时候（这是必然的），可以直接通过类似于下面的代码去
            取得HttpServletResponse。
         */
        HttpServletResponse response = ((ServletRequestAttributes)
                RequestContextHolder
                        .currentRequestAttributes())
                        .getResponse();
        Object redirectUri = joinPoint.proceed();
        if (!(redirectUri instanceof String) ||
                StringUtils.isEmpty(redirectUri) ||
                !PlaygroundUtils.IsValidUrl((String) redirectUri)) {
            return redirectUri;
        }

        log.info("Current debug.redirect is: " + ShouldRedirect);
        if (ShouldRedirect) {
            response.sendRedirect((String) redirectUri);
        }
        return redirectUri;
    }
}
