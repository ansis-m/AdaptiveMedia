package com.adaptivemedia.assignment.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulerService {

    private final DataPollingService dataPollingService;
    private final FetchLogService fetchLogService;
    private final Clock clock;

    //    @Scheduled(fixedRate = 300000)
    @Scheduled(fixedRate = 10000)
    public void pollForNewData() {
        log.info("Scheduler triggered data fetch");

        LocalDate lastFetch = fetchLogService.getLastFetchDate();
        final LocalDate today = LocalDate.now(clock);

        while (lastFetch.isBefore(today) || lastFetch.isEqual(today)) {
            dataPollingService.executeDataFetch(lastFetch);
            lastFetch = lastFetch.plusDays(1);
        }
    }
}
