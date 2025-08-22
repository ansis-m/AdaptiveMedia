package com.adaptivemedia.assignment.services;

import com.adaptivemedia.assignment.records.CommissionSummary;
import com.adaptivemedia.assignment.records.ConversionRate;
import com.adaptivemedia.assignment.records.ProductConversion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Record4;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.adaptivemedia.assignment.jooq.tables.SalesData.SALES_DATA;
import static org.jooq.impl.DSL.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesAnalyticsService {

    private final DSLContext dsl;

    public ConversionRate getConversionRate(String trackingCode, LocalDate fromDate, LocalDate toDate) {

        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);

        validateDateRange(fromDateTime, toDateTime);

        log.debug("Getting conversion rate for trackingCode: {}, dateRange: {} to {}",
                  trackingCode, fromDate, toDate);

        Record2<Integer, Integer> result = dsl
                .select(
                        count().as("totalVisits"),
                        count(SALES_DATA.SALE_DATE).as("totalSales")
                )
                .from(SALES_DATA)
                .where(SALES_DATA.TRACKING_CODE.eq(trackingCode))
                .and(SALES_DATA.VISIT_DATE.between(fromDateTime, toDateTime))
                .fetchOne();

        if (result == null) {
            return new ConversionRate(trackingCode, 0L, 0L, 0.0);
        }

        Long totalVisits = result.component1().longValue();
        Long totalSales = result.component2().longValue();
        Double conversionRate = totalVisits > 0 ? (totalSales.doubleValue() / totalVisits.doubleValue()) * 100 : 0.0;

        return new ConversionRate(trackingCode, totalVisits, totalSales, conversionRate);
    }


    public CommissionSummary getTotalCommission(String trackingCode, LocalDate fromDate, LocalDate toDate) {
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);

        validateDateRange(fromDateTime, toDateTime);

        log.debug("Getting total commission for trackingCode: {}, dateRange: {} to {}",
                  trackingCode, fromDate, toDate);

        Record2<BigDecimal, Integer> result = dsl
                .select(
                        sum(SALES_DATA.COMMISSION_AMOUNT).as("total_commission"),
                        count(SALES_DATA.SALE_DATE).as("total_sales")
                )
                .from(SALES_DATA)
                .where(SALES_DATA.TRACKING_CODE.eq(trackingCode))
                .and(SALES_DATA.VISIT_DATE.between(fromDateTime, toDateTime))
                .and(SALES_DATA.SALE_DATE.isNotNull())
                .fetchOne();

        if (result == null) {
            return new CommissionSummary(trackingCode, BigDecimal.ZERO, 0L);
        }

        BigDecimal totalCommission = result.component2() != null ? BigDecimal.valueOf(result.component2()) : BigDecimal.ZERO;
        Long totalSales = result.component2().longValue();

        return new CommissionSummary(trackingCode, totalCommission, totalSales);
    }


    public List<ProductConversion> getProductConversionRates(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);

        validateDateRange(fromDateTime, toDateTime);

        log.debug("Getting product conversion rates for dateRange: {} to {}", fromDate, toDate);

        List<Record4<String, Integer, Integer, Double>> results = dsl
                .select(
                        SALES_DATA.PRODUCT,
                        count().as("total_visits"),
                        count(SALES_DATA.SALE_DATE).as("total_sales"),
                        case_()
                                .when(count().gt(0),
                                      count(SALES_DATA.SALE_DATE).cast(Double.class)
                                                                 .div(count().cast(Double.class))
                                                                 .mul(100.0))
                                .otherwise(0.0)
                                .as("conversion_rate")
                )
                .from(SALES_DATA)
                .where(SALES_DATA.VISIT_DATE.between(fromDateTime, toDateTime))
                .and(SALES_DATA.PRODUCT.isNotNull())
                .groupBy(SALES_DATA.PRODUCT)
                .orderBy(field(name("conversion_rate")).desc())
                .fetch();

        return results.stream()
                      .map(record -> new ProductConversion(
                              record.component1(),
                              record.component2(),
                              record.component3(),
                              record.component4()
                      ))
                      .toList();
    }

    private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        if (startDate.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start date cannot be in the future");
        }
    }
}
