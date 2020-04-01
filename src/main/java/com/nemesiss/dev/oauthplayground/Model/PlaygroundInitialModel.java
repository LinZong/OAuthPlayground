package com.nemesiss.dev.oauthplayground.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.*;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaygroundInitialModel {


    @JsonProperty("host")
    @NotNull(message = "Token exchange host ip/url must not be null.")
    @NotEmpty(message = "Token exchange host ip/url must not be empty.")
    @NotBlank(message = "Token exchange host ip/url must not be blank.")
    private String Host;

    @JsonProperty("scopes")
    @NotNull
    private Map<String,Object> Scopes;

    @JsonProperty("credentials")
    @NotNull
    private Map<String, Object> Credentials;

    @Min(value = 1,message = "Playground expired timespan cannot less than 1 second.")
    @Max(value = 3600,message = "Playground expired timespan cannot larger than 1 hour.")
    @JsonProperty("expired_time")
    private int ExpiredTimeSpan;

    @Override
    public String toString() {
        return "PlaygroundInitialModel{" +
                "Host='" + Host + '\'' +
                ", Scopes=" + Scopes +
                ", Credentials=" + Credentials +
                '}';
    }
}
