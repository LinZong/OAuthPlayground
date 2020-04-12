package com.nemesiss.dev.oauthplayground.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizationRequestModel {


    public static class ResponseTypes {
        public static final String CODE = "code";
        public static final String IMPLICIT = "implicit";
        public static final String PASSWORD = "password";
    }

    @NotNull
    @NotEmpty
    @NotBlank
    @JsonProperty("response_type")
    private String ResponseType;

    @NotNull
    @NotEmpty
    @NotBlank
    @JsonProperty("redirect_uri")
    private String RedirectUri;


    @NotNull
    @JsonProperty("scopes")
    private List<String> Scopes;

    @NotNull
    @NotEmpty
    @NotBlank
    @JsonProperty("client_id")
    private String ClientID;

    @JsonProperty("state")
    private String State;
}
