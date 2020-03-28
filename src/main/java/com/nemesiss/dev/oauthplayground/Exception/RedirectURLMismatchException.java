package com.nemesiss.dev.oauthplayground.Exception;

public class RedirectURLMismatchException extends PlaygroundException {
    public RedirectURLMismatchException(String OriginHost, String CurrentHost) {
        super(String.format("Current redirect host %s is mismatch the playground initial redirect host %s.",
                OriginHost, CurrentHost),
                1003);
    }
}
