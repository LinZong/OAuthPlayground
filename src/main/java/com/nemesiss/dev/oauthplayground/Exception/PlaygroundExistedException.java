package com.nemesiss.dev.oauthplayground.Exception;

public class PlaygroundExistedException extends PlaygroundException {

    public PlaygroundExistedException(int code, String existedPlaygroundID) {
        super("Playground ID: " + existedPlaygroundID + " already existed.", code);
    }
}
