package com.adaptivemedia.assignment.services;

import com.adaptivemedia.assignment.jooq.Keys;
import com.adaptivemedia.assignment.jooq.tables.pojos.SalesData;
import com.adaptivemedia.assignment.jooq.tables.records.SalesDataRecord;
import com.adaptivemedia.assignment.properties.TrackingProperties;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.InsertReturningStep;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.adaptivemedia.assignment.jooq.Tables.SALES_DATA;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesDataService {

    private static final String DATA_FETCH_LOCK = "DATA_FETCH";
    private static final int MAX_CACHE_SIZE = 100_000;

    private final DSLContext dslContext;
    private final FetchLogService fetchLogService;
    private final LockService lockService;
    private final TrackingProperties trackingProperties;

    private final Set<Long> savedIdsCache = ConcurrentHashMap.newKeySet();

    @Transactional
    public void saveSalesData(List<SalesData> salesData, LocalDate date) {

        log.info("{} sales records passed to saveSalesData", salesData.size());
        try {
            List<InsertReturningStep<SalesDataRecord>> insertQueries = salesData.stream()
                    .filter(data -> !savedIdsCache.contains(data.getId()))  // Fast O(1) cache filter first
                    .filter(this::trackingIdsBelongToAdaptiveMediaPages)
                    .map(this::mapToStep)
                    .toList();

            lockService.acquireLockOrThrow(DATA_FETCH_LOCK);

            log.info("Passed {} records to JOOQ", insertQueries.size());
            int[] saved = dslContext.batch(insertQueries).execute();
            log.info("JOOQ saved {} records", Arrays.stream(saved).sum());

            fetchLogService.recordCompletedFetch(date);
            updateCache(salesData);
            log.info("Completed scheduled fetch for date: {}", date);
        } finally {
            lockService.releaseLock(DATA_FETCH_LOCK);
        }
    }

    private void updateCache(List<SalesData> salesData) {

        if (savedIdsCache.size() > MAX_CACHE_SIZE) {
            savedIdsCache.clear(); //jooq does nothing on conflict, so no big deal if we clear the cache
        }

        salesData.stream()
                 .map(SalesData::getId)
                 .forEach(savedIdsCache::add);
    }

    private boolean trackingIdsBelongToAdaptiveMediaPages(SalesData salesData) {
        return trackingProperties.getCodes().stream().anyMatch(salesData.getTrackingId()::startsWith);
    }

    private InsertReturningStep<SalesDataRecord> mapToStep(SalesData salesData) {
        return dslContext.insertInto(SALES_DATA)
                         .set(SALES_DATA.ID, salesData.getId())
                         .set(SALES_DATA.TRACKING_ID, salesData.getTrackingId())
                         .set(SALES_DATA.TRACKING_CODE, getTrackingCode(salesData))
                         .set(SALES_DATA.VISIT_DATE, salesData.getVisitDate())
                         .set(SALES_DATA.SALE_DATE, salesData.getSaleDate())
                         .set(SALES_DATA.SALE_PRICE, salesData.getSalePrice())
                         .set(SALES_DATA.PRODUCT, salesData.getProduct())
                         .set(SALES_DATA.COMMISSION_AMOUNT, salesData.getCommissionAmount())
                         .onConflictOnConstraint(Keys.SALES_DATA_PKEY)
                         .doNothing();
    }

    private static String getTrackingCode(SalesData salesData) {
        return salesData.getTrackingId().substring(0, 3);
    }
}
