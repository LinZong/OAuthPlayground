package com.nemesiss.dev.oauthplayground.Exception;

public class TokenExchangeCodeMismatchException extends PlaygroundException {
    public TokenExchangeCodeMismatchException(String CurrentCode) {
        super("The original declared token exchange code is mismatch with: " + CurrentCode + ".", 1005);
    }
}
