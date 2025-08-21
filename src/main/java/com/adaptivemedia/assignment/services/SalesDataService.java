package com.adaptivemedia.assignment.services;

import com.adaptivemedia.assignment.jooq.Keys;
import com.adaptivemedia.assignment.jooq.tables.pojos.SalesData;
import com.adaptivemedia.assignment.jooq.tables.records.SalesDataRecord;
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

    @Transactional
    public void saveSalesData(List<SalesData> salesDataList, LocalDate date) {

        lockService.acquireLockOrThrow(DATA_FETCH_LOCK);

        try {
            List<InsertReturningStep<SalesDataRecord>> insertQueries = salesDataList.stream()
                                                                                    .map(this::mapToStep)
                                                                                    .toList();

            int[] saved = dslContext.batch(insertQueries).execute();
            log.info("Saved {} records", Arrays.stream(saved).sum());

            fetchLogService.recordCompletedFetch(date);
            log.info("Completed scheduled fetch for date: {}", date);
        } finally {
            lockService.releaseLock(DATA_FETCH_LOCK);
        }
    }

    private InsertReturningStep<SalesDataRecord> mapToStep(SalesData salesData) {
        return dslContext.insertInto(SALES_DATA)
                         .set(SALES_DATA.ID, salesData.getId())
                         .set(SALES_DATA.TRACKING_ID, salesData.getTrackingId())
                         .set(SALES_DATA.VISIT_DATE, salesData.getVisitDate())
                         .set(SALES_DATA.SALE_DATE, salesData.getSaleDate())
                         .set(SALES_DATA.SALE_PRICE, salesData.getSalePrice())
                         .set(SALES_DATA.PRODUCT, salesData.getProduct())
                         .set(SALES_DATA.COMMISSION_AMOUNT, salesData.getCommissionAmount())
                         .onConflictOnConstraint(Keys.SALES_DATA_PKEY)
                         .doNothing();
    }
}
