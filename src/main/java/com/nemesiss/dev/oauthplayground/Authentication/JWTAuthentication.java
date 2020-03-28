package com.nemesiss.dev.oauthplayground.Authentication;

import com.nemesiss.dev.oauthplayground.Model.JWTTokenModel;
import com.nemesiss.dev.oauthplayground.Utils.JWTUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

public class JWTAuthentication extends AuthorizingRealm {
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return new SimpleAuthorizationInfo();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        JWTTokenModel token = (JWTTokenModel) authenticationToken;
        if (JWTUtils.Verify(token.getPlaygroundID(), token.getToken())) {
            Object credentials = token.getCredentials();
            return new SimpleAuthenticationInfo(credentials, credentials, token.getPlaygroundID());
        }
        throw new AuthenticationException("JWT Token Not Valid.");
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTTokenModel;
    }
}
