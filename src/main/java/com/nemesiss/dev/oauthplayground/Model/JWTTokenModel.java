package com.nemesiss.dev.oauthplayground.Model;

import com.auth0.jwt.JWT;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.shiro.authc.AuthenticationToken;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class JWTTokenModel implements AuthenticationToken {
    String Token;
    String PlaygroundID;

    @Override
    public Object getPrincipal() {
        return Arrays.stream(JWT.decode(Token)
                .getClaim("scopes")
                .asString()
                .split(","))
                .collect(Collectors.toSet());
    }

    @Override
    public Object getCredentials() {
        return JWT.decode(Token).getClaim("playground").asString();
    }
}
