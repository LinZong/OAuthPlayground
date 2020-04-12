package com.nemesiss.dev.oauthplayground.Utils;

import com.nemesiss.dev.oauthplayground.Model.AuthorizationRequestModel;

public class PlaygroundUtils {

    public static boolean IsPasswordMode(AuthorizationRequestModel AuthRequest) {
        return IsPasswordMode(AuthRequest.getResponseType());
    }

    public static boolean IsPasswordMode(String ResponseType) {
        return AuthorizationRequestModel.ResponseTypes.PASSWORD.equals(ResponseType);
    }
}
