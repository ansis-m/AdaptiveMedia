package com.adaptivemedia.assignment.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataPollingService {

    private static final String DATA_FETCH_LOCK = "DATA_FETCH";

    private final FetchLogService fetchLogService;
    private final LockService lockService;
    private final FetchService fetchService;
    private final Clock clock;


    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void fetchHistoricalData() {
        log.info("Fetching historical data");
        executeDataFetch();
    }

    @Transactional
    @Scheduled(fixedRate = 300000)
    public void pollForNewData() {
        log.info("Fetching new data");
        executeDataFetch();
    }

    private void executeDataFetch() {

        if (!lockService.tryAcquireLock(DATA_FETCH_LOCK)) {
            log.debug("Data fetch already in progress, skipping scheduled poll");
            return;
        }

        try {
            final LocalDateTime currentTime = LocalDateTime.now(clock);
            final LocalDateTime lastFetch = fetchLogService.getLastFetchTimestamp();
            log.info("Starting scheduled fetch from: {}", lastFetch);
            fetchService.fetchDataSince(lastFetch, currentTime);
            fetchLogService.recordFetch(currentTime);
        } finally {
            lockService.releaseLock(DATA_FETCH_LOCK);
        }
    }
}