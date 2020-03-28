package com.nemesiss.dev.oauthplayground.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponse extends CommonResponse {

    @JsonProperty("message")
    private String message;

    public ErrorResponse(int Code, String message) {
        super(Code);
        this.message = message;
    }
}
