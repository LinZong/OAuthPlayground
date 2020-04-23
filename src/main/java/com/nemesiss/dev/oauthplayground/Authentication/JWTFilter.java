package com.nemesiss.dev.oauthplayground.Authentication;

import com.nemesiss.dev.oauthplayground.Model.JWTTokenModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class JWTFilter extends BasicHttpAuthenticationFilter {


    /**
     * 判断用户是否想要登入。
     * 检测header里面是否包含Authorization字段即可
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader("Authorization");
        return authorization != null;
    }

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorization = httpServletRequest.getHeader("Authorization");
        String[] tokens = authorization.split(" ");
        String playgroundID = MatchPlaygroundID(((HttpServletRequest) request).getRequestURI());
        if(playgroundID != null && tokens.length == 2 && tokens[0].equals("Bearer") && !StringUtils.isEmpty(tokens[1])) {
            JWTTokenModel token = new JWTTokenModel(tokens[1],playgroundID);

            try {
                getSubject(request, response).login(token);
                return true;
            }
            catch (AuthenticationException ex) {}
        }
        return false;
    }

    private static String MatchPlaygroundID(String RequestURL) {
        String pattern = "/oauth2/([0-9]*)/";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(RequestURL);
        if(matcher.find()) {
            if(matcher.groupCount() > 0) {
                return (matcher.group(1));
            }
        }
        return null;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (isLoginAttempt(request, response)) {
            try {
                return executeLogin(request, response);
            } catch (Exception e) {
                response401(request, response);
            }
        }
        return false;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }
    /**
     * 将非法请求跳转到 /401
     */
    private void response401(ServletRequest req, ServletResponse resp) {
        try {
            HttpServletResponse httpServletResponse = (HttpServletResponse) resp;
            httpServletResponse.setStatus(401);
            PrintWriter pw = httpServletResponse.getWriter();
            pw.write("Unauthorized");
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }
}