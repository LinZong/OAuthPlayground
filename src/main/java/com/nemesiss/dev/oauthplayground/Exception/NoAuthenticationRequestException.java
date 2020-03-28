package com.nemesiss.dev.oauthplayground.Exception;

public class NoAuthenticationRequestException extends PlaygroundException {

    public NoAuthenticationRequestException(String PlaygroundID) {
        super("Cannot find previous login request for playground id: " + PlaygroundID, 1005);
    }
}
