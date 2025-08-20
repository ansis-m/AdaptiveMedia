package com.adaptivemedia.assignment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class ClockConfig {

    @Bean
    @Primary
    public Clock clock() {
        return Clock.system(ZoneId.of("UTC"));
    }
}