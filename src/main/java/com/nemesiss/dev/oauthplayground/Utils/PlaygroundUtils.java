package com.nemesiss.dev.oauthplayground.Utils;

import com.nemesiss.dev.oauthplayground.Model.AuthorizationRequestModel;
import org.apache.commons.validator.routines.UrlValidator;

public class PlaygroundUtils {

    private static class SingletonValidators {
        static UrlValidator UrlValidator = new UrlValidator(new String[]{"http", "https"});
    }

    public static boolean IsPasswordMode(AuthorizationRequestModel AuthRequest) {
        return IsPasswordMode(AuthRequest.getResponseType());
    }

    public static boolean IsPasswordMode(String ResponseType) {
        return AuthorizationRequestModel.ResponseTypes.PASSWORD.equals(ResponseType);
    }

    public static boolean IsValidUrl(String Url) {
        return SingletonValidators.UrlValidator.isValid(Url);
    }
}
