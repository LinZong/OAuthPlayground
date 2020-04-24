package com.nemesiss.dev.oauthplayground.Exception;

public class PlaygroundNotExistedException extends PlaygroundException {

    public PlaygroundNotExistedException(String existedPlaygroundID) {
        super("Playground ID: " + existedPlaygroundID + " not existed.", 1002);
    }
}
