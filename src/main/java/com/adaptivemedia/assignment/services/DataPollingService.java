package com.adaptivemedia.assignment.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataPollingService {

    private static final String DATA_FETCH_LOCK = "DATA_FETCH";

    private final FetchLogService fetchLogService;
    private final LockService lockService;


    @EventListener(ApplicationReadyEvent.class)
    public void fetchHistoricalData() {
        if (!lockService.tryAcquireLock(DATA_FETCH_LOCK)) {
            log.info("Data fetch already in progress, skipping historical fetch");
            return;
        }

        try {
            LocalDateTime lastFetch = fetchLogService.getLastFetchTimestamp()
                                                     .orElse(LocalDateTime.now().minusDays(30));
            log.info("Starting historical fetch from: {}", lastFetch);
//            fetchDataSince(lastFetch);
        } finally {
            lockService.releaseLock(DATA_FETCH_LOCK);
        }
    }

    @Scheduled(fixedRate = 300000) // 5 minutes
    public void pollForNewData() {
        if (!lockService.tryAcquireLock(DATA_FETCH_LOCK)) {
            log.debug("Data fetch already in progress, skipping scheduled poll");
            return;
        }

        try {
            LocalDateTime lastFetch = fetchLogService.getLastFetchTimestamp()
                                                     .orElse(LocalDateTime.now().minusYears(20));
            log.info("Starting scheduled fetch from: {}", lastFetch);
//            fetchDataSince(lastFetch);
        } finally {
            lockService.releaseLock(DATA_FETCH_LOCK);
        }
    }
}