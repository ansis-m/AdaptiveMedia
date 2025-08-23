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


    /**
     * Scheduled method that polls for new sales data at regular intervals.
     * <p>
     * On the first execution, this method fetches historical data starting from a bookmark date
     * stored by the {@link FetchLogService}. If no bookmark exists, it defaults to fetching
     * data from 30 days ago. The method processes data one day at a time to minimize batch sizes
     * and updates the bookmark after each successful fetch.
     * </p>
     * <p>
     * On subsequent executions, the method resumes fetching from where the bookmark indicates,
     * ensuring no data is missed and avoiding duplicate processing of previously fetched dates.
     * </p>
     * <p>
     * The method will continue fetching data day by day until it reaches the current date,
     * effectively catching up on any missed data since the last successful execution.
     * </p>
     *
     * @see FetchLogService#getLastFetchDate() for bookmark management
     * @see SalesDataClient#getSalesData(LocalDate, LocalDate) for data retrieval
     * @see SalesDataService#saveSalesData(List, LocalDate) for data persistence and bookmark updates
     *
     * @implNote Data is fetched in single-day batches to optimize performance and reduce
     *           memory usage for large historical datasets.
     */
    @Scheduled(fixedRateString = "${scheduler.polling.rate:300000}")
    public void pollForNewData() {
        log.info("Scheduler triggered data fetch");

        LocalDate lastFetchDate = fetchLogService.getLastFetchDate().orElseGet(() -> LocalDate.now(clock).minusDays(30));
        final LocalDate today = LocalDate.now(clock);

        while (lastFetchDate.isBefore(today) || lastFetchDate.isEqual(today)) {
            List<SalesData> salesData = salesDataClient.getSalesData(lastFetchDate, lastFetchDate); //we fetch in the smallest batches possible - one day at a time
            salesDataService.saveSalesData(salesData, lastFetchDate);
            lastFetchDate = lastFetchDate.plusDays(1);
        }
    }
}
