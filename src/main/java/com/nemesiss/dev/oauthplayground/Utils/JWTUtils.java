package com.nemesiss.dev.oauthplayground.Utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.AbstractMap;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class JWTUtils {


    public static String Secret;


    public static long EXPIRE_TIME;

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        Secret = secret;
        algorithm = Algorithm.HMAC256(Secret);
    }

    // Cached Algorithm.
    private static Algorithm algorithm;

    @Value("${jwt.expired}")
    public void setExpireTime(long expireTime) {
        EXPIRE_TIME = expireTime;
    }


    public static String Sign(String PlaygroundID, List<String> ApprovedScopes) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        return JWT.create()
                .withClaim("playground", PlaygroundID)
                .withClaim("scopes", ApprovedScopes.stream().reduce((a, b) -> a + "," + b).orElse(""))
                .withExpiresAt(date)
                .sign(Algorithm.HMAC256(Secret));
    }

    public static boolean Verify(String PlaygroundID, String Token) {
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
    public static Optional<String> GetTokenFromHeader(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        String[] tokens = authorization.split(" ");
        String token = null;
        if(tokens.length == 2 && tokens[0].equals("Bearer") && !StringUtils.isEmpty(tokens[1])) {
            token = tokens[1];
        }
        return Optional.ofNullable(token);
    }

    public static ResponseEntity<AbstractMap.SimpleEntry<String, String>> GetTokenResponse(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json");
        AbstractMap.SimpleEntry<String, String> body = new AbstractMap.SimpleEntry<>("token", token);
        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }
}