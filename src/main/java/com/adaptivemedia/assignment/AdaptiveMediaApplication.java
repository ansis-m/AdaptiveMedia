package com.adaptivemedia.assignment;

import com.adaptivemedia.assignment.jooq.tables.pojos.SalesData;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AdaptiveMediaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdaptiveMediaApplication.class, args);
        SalesData data = new SalesData();
    }

}
