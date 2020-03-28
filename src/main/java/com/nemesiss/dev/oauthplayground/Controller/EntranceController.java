package com.nemesiss.dev.oauthplayground.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nemesiss.dev.oauthplayground.Exception.PlaygroundException;
import com.nemesiss.dev.oauthplayground.Exception.PlaygroundExistedException;
import com.nemesiss.dev.oauthplayground.Model.*;
import com.nemesiss.dev.oauthplayground.ParamValidator.PlaygroundIDValidator;
import com.nemesiss.dev.oauthplayground.Utils.SnowFlakeId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("oauth2/")
@EnableAutoConfiguration
@Validated
public class EntranceController {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SnowFlakeId snowFlakeId;

    @Value("${deploy.url.prefix}")
    String DeployUrlPrefix;

    @PostMapping(value = "create", produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    Object CreatePlayground(@Valid @RequestBody PlaygroundInitialModel PlaygroundInitInfo, HttpServletRequest request) throws PlaygroundException, JsonProcessingException {
        long NewPlayground = snowFlakeId.nextId();

        UriComponents loginPath = UriComponentsBuilder
                .fromHttpUrl(DeployUrlPrefix)
                .path("/oauth2/")
                .path(String.valueOf(NewPlayground))
                .path("/login")
                .build();
        UriComponents logPath = UriComponentsBuilder
                .fromHttpUrl(DeployUrlPrefix)
                .path("/oauth2/")
                .path(String.valueOf(NewPlayground))
                .path("/logging")
                .build();
        WritePlaygroundInfoToCache(NewPlayground, PlaygroundInitInfo);
        return new PlaygroundInfoModel(loginPath.toUriString(), logPath.toUriString());
    }

    @PlaygroundIDValidator
    @RequestMapping(value = "{PlaygroundID}/login", method = RequestMethod.GET)
    public Object HandleLoginRequest(@PathVariable("PlaygroundID")
                                             String PlaygroundID,

                                     @Pattern(regexp = "^(code|implict|password)$")
                                     @RequestParam("response_type")
                                             String ResponseType,
                                     @NotBlank
                                     @NotEmpty
                                     @RequestParam("redirect_uri")
                                             String RedirectUri,
                                     @NotBlank
                                     @NotEmpty
                                     @RequestParam("scopes")
                                             String Scopes,
                                     @NotBlank
                                     @NotEmpty
                                     @RequestParam("client_id")
                                             String ClientId,
                                     @RequestParam(value = "state", required = false)
                                             String State,
                                     HttpSession session) {
        return "OK!";
    }

    @PlaygroundIDValidator
    @RequestMapping(value = "{PlaygroundID}/login", method = RequestMethod.POST)
    public Object HandleLoginRequest(@PathVariable("PlaygroundID") String PlaygroundID,
                                     @RequestBody AuthorizationTokenExchangeModel tokenExchange) {
        return null;
    }


    private boolean WritePlaygroundInfoToCache(long PlaygroundID, PlaygroundInitialModel PlaygroundInitInfo)
            throws PlaygroundExistedException, JsonProcessingException {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        boolean addStatus = Optional.ofNullable(valueOperations
                .setIfAbsent(String.valueOf(PlaygroundID),
                        objectMapper.writeValueAsString(PlaygroundInitInfo),
                        PlaygroundInitInfo.getExpiredTimeSpan(),
                        TimeUnit.SECONDS))
                .orElseThrow(() -> new IllegalStateException("Cannot add key to redis instance."));
        if (!addStatus)
            throw new PlaygroundExistedException(1001, String.valueOf(PlaygroundID));
        return true;
    }
}
