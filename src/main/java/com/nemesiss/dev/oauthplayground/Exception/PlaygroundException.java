package com.nemesiss.dev.oauthplayground.Exception;

import lombok.Getter;

@Getter
public class PlaygroundException extends Exception {

    private int Code;

    public PlaygroundException(String message, int code) {
        super(message);
        Code = code;
    }
}
