package com.adaptivemedia.assignment.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

import static com.adaptivemedia.assignment.jooq.Tables.FETCH_LOG;

@Service
@Slf4j
@RequiredArgsConstructor
public class FetchLogService {

    private final DSLContext dsl;

    public Optional<LocalDate> getLastFetchDate() {
        LocalDate result = dsl.select(FETCH_LOG.FETCH_DATE)
                              .from(FETCH_LOG)
                              .orderBy(FETCH_LOG.ID.desc())
                              .limit(1)
                              .fetchOne(FETCH_LOG.FETCH_DATE);

        return Optional.ofNullable(result);
    }

    public void recordCompletedFetch(LocalDate date) {
        dsl.insertInto(FETCH_LOG)
           .set(FETCH_LOG.FETCH_DATE, date)
           .execute();

        log.info("Recorded fetch date: {}", date);
    }
}
