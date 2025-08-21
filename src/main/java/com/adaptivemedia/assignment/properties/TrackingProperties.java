package com.adaptivemedia.assignment.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import java.util.List;

@ConfigurationProperties(prefix = "app.tracking")
@RefreshScope
@Component
@Getter
@Setter
public class TrackingProperties {
    private List<String> codes = List.of("ABB", "TBS", "EKW");
}