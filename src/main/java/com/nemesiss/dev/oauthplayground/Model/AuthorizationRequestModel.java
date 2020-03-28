package com.nemesiss.dev.oauthplayground.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class AuthorizationRequestModel {
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
