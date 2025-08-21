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

import static com.adaptivemedia.assignment.jooq.Tables.SALES_DATA;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesDataService {

    private static final String DATA_FETCH_LOCK = "DATA_FETCH";

    private final DSLContext dslContext;
    private final FetchLogService fetchLogService;
    private final LockService lockService;
    private final TrackingProperties trackingProperties;

    @Transactional
    public void saveSalesData(List<SalesData> salesData, LocalDate date) {

        try {
            List<InsertReturningStep<SalesDataRecord>> insertQueries = salesData.stream()
                    .filter(this::trackingIdsBelongToAdaptiveMediaPages)
                    .map(this::mapToStep)
                    .toList();

            lockService.acquireLockOrThrow(DATA_FETCH_LOCK);

            int[] saved = dslContext.batch(insertQueries).execute();
            log.info("Saved {} records", Arrays.stream(saved).sum());

            fetchLogService.recordCompletedFetch(date);
            log.info("Completed scheduled fetch for date: {}", date);
        } finally {
            lockService.releaseLock(DATA_FETCH_LOCK);
        }
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
