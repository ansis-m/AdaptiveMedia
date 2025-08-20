package com.adaptivemedia.assignment.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.adaptivemedia.assignment.jooq.Tables.FETCH_LOG;

@Service
@Slf4j
@RequiredArgsConstructor
public class FetchLogService {

    private final DSLContext dsl;

    public LocalDateTime getLastFetchTimestamp() {
        LocalDateTime result = dsl.select(FETCH_LOG.FETCH_TIMESTAMP)
                                            .from(FETCH_LOG)
                                            .orderBy(FETCH_LOG.ID.desc())
                                            .limit(1)
                                            .fetchOne(FETCH_LOG.FETCH_TIMESTAMP);

        log.debug("Retrieved last fetch timestamp: {}", result);
        return result;
    }

    public void recordFetch(LocalDateTime timestamp) {
        dsl.insertInto(FETCH_LOG)
           .set(FETCH_LOG.FETCH_TIMESTAMP, timestamp)
           .execute();

        log.info("Recorded fetch timestamp: {}", timestamp);
    }
}
