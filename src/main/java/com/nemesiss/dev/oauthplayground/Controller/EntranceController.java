package com.nemesiss.dev.oauthplayground.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nemesiss.dev.oauthplayground.Exception.PlaygroundException;
import com.nemesiss.dev.oauthplayground.Exception.PlaygroundExistedException;
import com.nemesiss.dev.oauthplayground.Model.ErrorResponse;
import com.nemesiss.dev.oauthplayground.Model.PlaygroundInfoModel;
import com.nemesiss.dev.oauthplayground.Model.PlaygroundInitialModel;
import com.nemesiss.dev.oauthplayground.Utils.SnowFlakeId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("oauth2/")
@EnableAutoConfiguration
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

    private boolean WritePlaygroundInfoToCache(long PlaygroundID, PlaygroundInitialModel PlaygroundInitInfo) throws PlaygroundExistedException, JsonProcessingException {
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
