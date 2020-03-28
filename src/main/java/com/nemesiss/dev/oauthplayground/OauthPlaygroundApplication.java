package com.nemesiss.dev.oauthplayground;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class OauthPlaygroundApplication {

    public static void main(String[] args) {
        SpringApplication.run(OauthPlaygroundApplication.class, args);
    }
}

