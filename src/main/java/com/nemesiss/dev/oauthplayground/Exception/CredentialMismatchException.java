package com.nemesiss.dev.oauthplayground.Exception;

public class CredentialMismatchException extends PlaygroundException {
    public CredentialMismatchException() {
        super("Authentication credentials mismatch.", 1004);
    }
}
