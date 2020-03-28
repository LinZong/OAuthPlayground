package com.nemesiss.dev.oauthplayground.Model;

import com.auth0.jwt.JWT;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.shiro.authc.AuthenticationToken;

@Data
@AllArgsConstructor
public class JWTTokenModel implements AuthenticationToken {
    String Token;
    String PlaygroundID;

    @Override
    public Object getPrincipal() {
        return JWT.decode(Token).getClaim("playground").asString();
    }

    @Override
    public Object getCredentials() {
        return JWT.decode(Token).getClaim("playground").asString();
    }
}
