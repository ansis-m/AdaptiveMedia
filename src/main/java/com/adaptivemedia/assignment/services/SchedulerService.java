package com.adaptivemedia.assignment.services;


import com.adaptivemedia.assignment.apiClients.SalesDataClient;
import com.adaptivemedia.assignment.jooq.tables.pojos.SalesData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulerService {

    private final SalesDataClient salesDataClient;
    private final SalesDataService salesDataService;
    private final FetchLogService fetchLogService;
    private final Clock clock;

    //    @Scheduled(fixedRate = 300000)
    @Scheduled(fixedRate = 10000)
    public void pollForNewData() {
        log.info("Scheduler triggered data fetch");

        LocalDate lastFetchDate = fetchLogService.getLastFetchDate();
        final LocalDate today = LocalDate.now(clock);

        while (lastFetchDate.isBefore(today) || lastFetchDate.isEqual(today)) {
            List<SalesData> salesData = salesDataClient.getSalesData(lastFetchDate, lastFetchDate);
            salesDataService.saveSalesData(salesData, lastFetchDate);
            lastFetchDate = lastFetchDate.plusDays(1);
        }
    }
}
