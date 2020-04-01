package com.nemesiss.dev.oauthplayground.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaygroundInfoModel {
    private String login;
    private String logging;
    private String authentication;

    @JsonProperty("token_exchange")
    private String tokenExchange;
    @JsonProperty("scope_approve")
    private String scopeApprove;
    @JsonProperty("read_secret")
    private String readSecret;

    public PlaygroundInfoModel(String... args) {
        if (args.length < 6) {
            throw new IllegalArgumentException("Arguments cannot match all fields. Current argument length is: " + args.length);
        }
        login = args[0];
        logging = args[1];
        authentication = args[2];
        tokenExchange = args[3];
        scopeApprove = args[4];
        readSecret = args[5];
    }

    public PlaygroundInfoModel() {
    }
}
