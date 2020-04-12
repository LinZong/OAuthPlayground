package com.nemesiss.dev.oauthplayground.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@EnableAutoConfiguration
@RestController
@RequestMapping("oauth2/frontend/")
@Validated
public class FrontEndInteractController {

    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping("playgroundexists")
    public String DetectPlaygroundExists(@RequestParam("id")
                                         @NotEmpty
                                         @NotBlank
                                                 String PlaygroundID,
                                         HttpServletResponse response) {

        if (null == redisTemplate.opsForValue().get(PlaygroundID)) {
            response.setStatus(400);
        }
        return "";
    }
}
