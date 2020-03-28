package com.nemesiss.dev.oauthplayground.Exception;

public class PlaygroundNotExistedException extends PlaygroundException {

    public PlaygroundNotExistedException(int code, String existedPlaygroundID) {
        super("Playground ID: " + existedPlaygroundID + " not existed.", code);
    }
}
