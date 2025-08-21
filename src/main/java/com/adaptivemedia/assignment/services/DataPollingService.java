package com.adaptivemedia.assignment.services;

import com.adaptivemedia.assignment.apiClients.SalesDataClient;
import com.adaptivemedia.assignment.jooq.tables.pojos.SalesData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataPollingService {

    private final SalesDataClient salesDataClient;
    private final SalesDataService salesDataService;


    void executeDataFetch(LocalDate date) {
        
//        1) fetch data for the day (here for now)
//        2)  call external service with transactional to save

        List<SalesData> data = salesDataClient.getSalesData(date, date);
//        data.forEach(System.out::println);
        salesDataService.saveSalesData(data, date);
    }
}