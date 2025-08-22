package com.adaptivemedia.assignment.config;

import com.adaptivemedia.assignment.properties.TrackingProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TrackingProperties.class)
public class AppConfig {
}
