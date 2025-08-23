package com.adaptivemedia.assignment.config;

import com.adaptivemedia.assignment.apiClients.SalesDataClient;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public SalesDataClient mockSalesDataClient() {
        return Mockito.mock(SalesDataClient.class);
    }
}