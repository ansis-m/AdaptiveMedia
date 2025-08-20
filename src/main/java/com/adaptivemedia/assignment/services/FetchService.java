package com.adaptivemedia.assignment.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class FetchService {

    public void fetchDataSince(LocalDateTime lastFetch, LocalDateTime currentTime) {
        log.info("Fetching data since: {}", lastFetch);
    }
}
