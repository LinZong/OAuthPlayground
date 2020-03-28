package com.nemesiss.dev.oauthplayground.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nemesiss.dev.oauthplayground.Exception.*;
import com.nemesiss.dev.oauthplayground.Model.*;
import com.nemesiss.dev.oauthplayground.ParamValidator.PlaygroundIDValidator;
import com.nemesiss.dev.oauthplayground.Utils.EqualUtils;
import com.nemesiss.dev.oauthplayground.Utils.SnowFlakeId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("oauth2/")
@EnableAutoConfiguration
@Validated
public class EntranceController {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SnowFlakeId snowFlakeId;

    @Value("${deploy.url.prefix}")
    String DeployUrlPrefix;


    private static final String TOKEN_EXCHANGE_HASH_POOL = "TOKEN_EXCHANGE_HASH_POOL";
    private static final String AUTHENTICATION_INFO_HASH_POOL = "AUTHENTICATION_INFO_HASH_POOL";

    private UriComponentsBuilder AuthUrlBuilder(String PlaygroundID, String FunctionName) {
        return UriComponentsBuilder
                .fromHttpUrl(DeployUrlPrefix)
                .path("/oauth2/")
                .path(PlaygroundID)
                .path("/" + FunctionName);
    }

    private UriComponentsBuilder AuthUrlBuilder(long PlaygroundID, String FunctionName) {
        return AuthUrlBuilder(String.valueOf(PlaygroundID), FunctionName);
    }

    @PostMapping(value = "create", produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody
    Object CreatePlayground(@Valid @RequestBody PlaygroundInitialModel PlaygroundInitInfo, HttpServletRequest request) throws PlaygroundException, JsonProcessingException {
        long NewPlayground = snowFlakeId.nextId();

        UriComponents loginPath = AuthUrlBuilder(NewPlayground, "login").build();
        UriComponents logPath = AuthUrlBuilder(NewPlayground, "logging").build();

        WritePlaygroundInfoToCache(NewPlayground, PlaygroundInitInfo);
        return new PlaygroundInfoModel(loginPath.toUriString(), logPath.toUriString());
    }

    @PlaygroundIDValidator
    @RequestMapping(value = "{PlaygroundID}/login", method = RequestMethod.GET)
    public Object HandleLoginRequest(@PathVariable("PlaygroundID")
                                             String PlaygroundID,
                                     @Pattern(regexp = "^(code|implict)$")
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
                                     HttpSession session) throws PlaygroundNotExistedException, RedirectURLMismatchException, JsonProcessingException {

        MarkAsLogout(PlaygroundID, ClientId, session);


        UriComponents redirectExchangeTokenUrl = UriComponentsBuilder.fromHttpUrl(RedirectUri).build();
        PlaygroundInitialModel playground = GetPlaygroundInitialInfo(PlaygroundID);

        if (!playground.getHost().equals(redirectExchangeTokenUrl.getHost())) {
            throw new RedirectURLMismatchException(playground.getHost(), redirectExchangeTokenUrl.getHost());
        }

        List<String> scopes = Arrays.stream(Scopes.split(",")).collect(Collectors.toList());
        AuthorizationRequestModel AuthRequest = new AuthorizationRequestModel(ResponseType, RedirectUri, scopes, ClientId, State);

        redisTemplate.opsForHash().put(AUTHENTICATION_INFO_HASH_POOL, PlaygroundID, objectMapper.writeValueAsString(AuthRequest));

        return AuthUrlBuilder(PlaygroundID, "authentication")
                .queryParam("client_id", ClientId)
                .queryParam("redirect_uri", RedirectUri).toUriString();
    }

    @PlaygroundIDValidator
    @RequestMapping(value = "{PlaygroundID}/authentication", method = RequestMethod.GET)
    public Object HandleAuthenticationRequest(@PathVariable("PlaygroundID") String PlaygroundID,
                                              @NotBlank
                                              @NotEmpty
                                              @RequestParam("client_id")
                                                      String ClientId,
                                              @NotBlank
                                              @NotEmpty
                                              @RequestParam("redirect_uri") String RedirectUri,
                                              HttpSession session) throws NoAuthenticationRequestException, JsonProcessingException, RedirectURLMismatchException {
        if (DetectLogin(PlaygroundID, ClientId, session)) {
            UriComponentsBuilder redirectUri = UriComponentsBuilder.fromHttpUrl(RedirectUri);
            AuthorizationRequestModel AuthRequest = GetAuthenticationRequestModel(PlaygroundID);
            if (!AuthRequest.getRedirectUri().equals(redirectUri.toUriString())) {
                throw new RedirectURLMismatchException(AuthRequest.getRedirectUri(), RedirectUri);
            }
            // 执行立即登录流程.
            return GenerateTokenExchangeUrl(redirectUri, PlaygroundID, AuthRequest.getState());
        }
        return "只是一个普通的登录页. 没人做前端所以不会返回什么. 请使用POST验证.";
    }

    @PlaygroundIDValidator
    @RequestMapping(value = "{PlaygroundID}/authentication", method = RequestMethod.POST)
    public Object TokenExchange(@PathVariable("PlaygroundID")
                                            String PlaygroundID,
                                @RequestParam("code") String code,
                                HttpServletRequest request) throws JsonProcessingException, PlaygroundNotExistedException, RedirectURLMismatchException {

        String host = request.getHeader("host");
        if(host == null) {
            host = request.getHeader("Host");
        }
        if(host == null) {
            host = request.getRemoteHost();
        }

        PlaygroundInitialModel playground = GetPlaygroundInitialInfo(PlaygroundID);
        if(!playground.getHost().equals(host)) {
            throw new RedirectURLMismatchException(playground.getHost(), host);
        }

        return "OK";
    }

    @PlaygroundIDValidator
    @RequestMapping(value = "{PlaygroundID}/authentication", method = RequestMethod.POST)
    public Object HandleAuthenticationRequest(@PathVariable("PlaygroundID")
                                                      String PlaygroundID,
                                              @NotBlank
                                              @NotEmpty
                                              @RequestParam("redirect_uri")
                                                      String RedirectUri,
                                              @NotBlank
                                              @NotEmpty
                                              @RequestParam("client_id")
                                                      String ClientId,
                                              @RequestBody
                                                      Map<String, Object> credentials,
                                              HttpServletResponse response,
                                              HttpSession session
    ) throws PlaygroundNotExistedException, CredentialMismatchException, JsonProcessingException, NoAuthenticationRequestException, RedirectURLMismatchException {

        PlaygroundInitialModel playground = GetPlaygroundInitialInfo(PlaygroundID);
        Map<String, Object> originCredentials = playground.getCredentials();

        AuthorizationRequestModel AuthRequest = GetAuthenticationRequestModel(PlaygroundID);

        UriComponentsBuilder redirectUri = UriComponentsBuilder.fromHttpUrl(RedirectUri);

        if (!AuthRequest.getRedirectUri().equals(redirectUri.toUriString())) {
            throw new RedirectURLMismatchException(AuthRequest.getRedirectUri(), RedirectUri);
        }

        if (!EqualUtils.CompareTwoMap(originCredentials, credentials)) {
            response.setStatus(401);
            throw new CredentialMismatchException();
        }
        MarkAsLogin(PlaygroundID, ClientId, session);
        return GenerateTokenExchangeUrl(redirectUri, PlaygroundID, AuthRequest.getState());
    }

    private String GenerateTokenExchangeUrl(UriComponentsBuilder redirectUri, String PlaygroundID, String state) {
        String code = UUID.randomUUID().toString();
        redirectUri = redirectUri.queryParam("code", code);
        if (state != null) {
            redirectUri = redirectUri.queryParam("state", state);
        }
        redisTemplate.opsForHash().put(TOKEN_EXCHANGE_HASH_POOL, PlaygroundID, code);
        return redirectUri.toUriString();
    }

    private PlaygroundInitialModel GetPlaygroundInitialInfo(String PlaygroundID) throws PlaygroundNotExistedException, JsonProcessingException {
        String content = redisTemplate.opsForValue().get(PlaygroundID);
        if (content == null || StringUtils.isEmpty(content)) {
            throw new PlaygroundNotExistedException(PlaygroundID);
        }
        return objectMapper.readValue(content, PlaygroundInitialModel.class);
    }

    private AuthorizationRequestModel GetAuthenticationRequestModel(String PlaygroundID) throws JsonProcessingException, NoAuthenticationRequestException {
        Object content = GetHashObject(AUTHENTICATION_INFO_HASH_POOL, PlaygroundID);
        if (!(content instanceof String)) {
            throw new NoAuthenticationRequestException(PlaygroundID);
        }
        return objectMapper.readValue((String) content, AuthorizationRequestModel.class);
    }


    // =========== SESSION 相关操作 ===============
    private void MarkAsLogin(String PlaygroundID, String ClientId, HttpSession session) {
        session.setAttribute(PlaygroundID + ClientId, 1);
    }

    private boolean DetectLogin(String PlaygroundID, String ClientId, HttpSession session) {
        return session.getAttribute(PlaygroundID + ClientId) != null;
    }

    private void MarkAsLogout(String PlaygroundID, String ClientId, HttpSession session) {
        session.removeAttribute(PlaygroundID + ClientId);
    }

    // ============ Redis 操作工具方法 ===========

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

    private Object GetHashObject(String HashPoolKey, String Key) {
        return redisTemplate.opsForHash().get(HashPoolKey, Key);
    }

}
