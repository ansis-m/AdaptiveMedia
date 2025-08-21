package com.adaptivemedia.assignment.apiClients;

import com.adaptivemedia.assignment.jooq.tables.pojos.SalesData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.LocalDate;
import java.util.List;

@HttpExchange
public interface SalesDataClient {

    @GetExchange("/sales-data")
    List<SalesData> getSalesData(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate);
}

@Configuration
class HttpInterfaceConfig {

    @Value("${api.sales.base-url:http://localhost:8081}")
    private String baseUrl;

    @Bean
    public SalesDataClient salesDataClient() {
        WebClient webClient = WebClient.builder()
                                       .baseUrl(baseUrl)
                                       .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient))
                .build();

        return factory.createClient(SalesDataClient.class);
    }
}