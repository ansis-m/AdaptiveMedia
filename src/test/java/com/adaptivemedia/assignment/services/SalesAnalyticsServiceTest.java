package com.adaptivemedia.assignment.services;

import com.adaptivemedia.assignment.BaseIntegrationTest;

import com.adaptivemedia.assignment.records.ConversionRate;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


@Sql(scripts = "/db/testdata/Test_Sales_Data.sql")
@Sql(scripts = "/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Transactional
public class SalesAnalyticsServiceTest extends BaseIntegrationTest {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SalesAnalyticsService salesAnalyticsService;

    @Test
    void shouldCalculateConversionRateForABB() {
        // Given
        String trackingCode = "ABB";
        LocalDate fromDate = LocalDate.of(2024, 1, 15);
        LocalDate toDate = LocalDate.of(2024, 1, 15);

        // When
        ConversionRate result = salesAnalyticsService.getConversionRate(trackingCode, fromDate, toDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.trackingCode()).isEqualTo(trackingCode);
        assertThat(result.visitActions()).isEqualTo(3L);
        assertThat(result.purchaseAction()).isEqualTo(3L);
        assertThat(result.conversionRate()).isEqualTo(100.0);
    }

}
