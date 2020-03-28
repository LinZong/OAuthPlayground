package com.nemesiss.dev.oauthplayground.Utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JWTUtils {


    public static String Secret;


    public static long EXPIRE_TIME;

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        Secret = secret;
    }
    @Value("${jwt.expired}")
    public void setExpireTime(long expireTime) {
        EXPIRE_TIME = expireTime;
    }
    public static String Sign(String PlaygroundID) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        return JWT.create()
                .withClaim("playground", PlaygroundID)
                .withExpiresAt(date)
                .sign(Algorithm.HMAC256(Secret));
    }

    public static boolean Verify(String PlaygroundID, String Token) {
        Algorithm algorithm = Algorithm.HMAC256(Secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withClaim("playground", PlaygroundID)
                .build();
        try {
            verifier.verify(Token);
        } catch (JWTVerificationException ignore) {
            return false;
        }
        return true;
    }
}
