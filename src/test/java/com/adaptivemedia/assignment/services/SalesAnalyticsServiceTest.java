package com.adaptivemedia.assignment.services;

import com.adaptivemedia.assignment.BaseIntegrationTest;

import com.adaptivemedia.assignment.records.CommissionSummary;
import com.adaptivemedia.assignment.records.ConversionRate;
import com.adaptivemedia.assignment.records.ProductConversion;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@Sql(scripts = "/db/testdata/Test_Sales_Data.sql")
@Sql(scripts = "/db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Transactional
public class SalesAnalyticsServiceTest extends BaseIntegrationTest {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SalesAnalyticsService salesAnalyticsService;


    @ParameterizedTest
    @MethodSource("conversionArgsProvider")
    void shouldCalculateConversionRate(
            String trackingCode,
            LocalDate fromDate,
            LocalDate toDate,
            Long visitAction,
            Long purchaseAction,
            Double conversionRate
    ) {

        // When
        ConversionRate result = salesAnalyticsService.getConversionRate(trackingCode, fromDate, toDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.trackingCode()).isEqualTo(trackingCode);
        assertThat(result.visitActions()).isEqualTo(visitAction);
        assertThat(result.purchaseAction()).isEqualTo(purchaseAction);
        assertThat(result.conversionRate()).isEqualTo(conversionRate);
    }

    @ParameterizedTest
    @MethodSource("commissionArgsProvider")
    void shouldCalculateCommission(
            String trackingCode,
            LocalDate fromDate,
            LocalDate toDate,
            Long purchaseAction,
            BigDecimal commission
    ) {

        // When
        CommissionSummary result = salesAnalyticsService.getTotalCommission(trackingCode, fromDate, toDate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.trackingCode()).isEqualTo(trackingCode);
        assertThat(result.purchaseActions()).isEqualTo(purchaseAction);
        assertThat(result.totalCommission()).isEqualTo(commission);
    }



    @Nested
    class ProductConversionRateTests {

        @Test
        void shouldCalculateProductConversions2024() {

            // Given
            LocalDate fromDate = LocalDate.of(2024, 1, 15);
            LocalDate toDate = LocalDate.of(2024, 1, 20);

            // When
            List<ProductConversion> result = salesAnalyticsService.getProductConversionRates(fromDate, toDate);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.size()).isEqualTo(3);

            result.forEach(System.out::println);

            ProductConversion first = result.getFirst();
            ProductConversion second = result.get(1);
            ProductConversion third = result.get(2);

            assertThat(first.product()).isEqualTo("Basic Widget");
            assertThat(first.visitActions()).isEqualTo(1L);
            assertThat(first.purchaseActions()).isEqualTo(1L);
            assertThat(first.conversionRate()).isEqualTo(100.0);

            assertThat(second.product()).isEqualTo("Premium Widget");
            assertThat(second.visitActions()).isEqualTo(1L);
            assertThat(second.purchaseActions()).isEqualTo(1L);
            assertThat(second.conversionRate()).isEqualTo(100.0);

            assertThat(third.product()).isEqualTo("Deluxe Widget");
            assertThat(third.visitActions()).isEqualTo(3L);
            assertThat(third.purchaseActions()).isEqualTo(2L);
            assertThat(third.conversionRate()).isBetween(66.6, 66.7);
        }

        @Test
        void shouldCalculateProductConversions2025() {

            // Given
            LocalDate fromDate = LocalDate.of(2024, 1, 15);
            LocalDate toDate = LocalDate.of(2025, 1, 20);

            // When
            List<ProductConversion> result = salesAnalyticsService.getProductConversionRates(fromDate, toDate);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.size()).isEqualTo(3);

            result.forEach(System.out::println);

            ProductConversion first = result.getFirst();
            ProductConversion second = result.get(1);
            ProductConversion third = result.get(2);

            assertThat(first.product()).isEqualTo("Basic Widget");
            assertThat(first.visitActions()).isEqualTo(2L);
            assertThat(first.purchaseActions()).isEqualTo(2L);
            assertThat(first.conversionRate()).isEqualTo(100.0);

            assertThat(second.product()).isEqualTo("Premium Widget");
            assertThat(second.visitActions()).isEqualTo(2L);
            assertThat(second.purchaseActions()).isEqualTo(2L);
            assertThat(second.conversionRate()).isEqualTo(100.0);

            assertThat(third.product()).isEqualTo("Deluxe Widget");
            assertThat(third.visitActions()).isEqualTo(6L);
            assertThat(third.purchaseActions()).isEqualTo(4L);
            assertThat(third.conversionRate()).isBetween(66.6, 66.7);
        }
    }

    public static Stream<Arguments> conversionArgsProvider() {
        return Stream.of(
                Arguments.of("ABB", LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 15), 3L, 3L, 100.0),
                Arguments.of("ABB", LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 16), 4L, 3L, 75.0),
                Arguments.of("ABB", LocalDate.of(2024, 1, 18), LocalDate.of(2024, 1, 18), 1L, 1L, 100.0),
                Arguments.of("ABB", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 20), 5L, 4L, 80.0),
                Arguments.of("TBS", LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 15), 0L, 0L, 0.0),
                Arguments.of("TBS", LocalDate.of(2024, 1, 16), LocalDate.of(2024, 1, 16), 1L, 0L, 0.0),
                Arguments.of("EKW", LocalDate.of(2024, 1, 15), LocalDate.of(2024, 1, 15), 0L, 0L, 0.0),
                Arguments.of("EKW", LocalDate.of(2024, 1, 16), LocalDate.of(2024, 1, 16), 1L, 1L, 100.0)
        );
    }

    public static Stream<Arguments> commissionArgsProvider() {
        return Stream.of(
                Arguments.of("ABB", LocalDate.of(2025, 1, 15), LocalDate.of(2025, 1, 15), 3L, BigDecimal.valueOf(40.48)),
                Arguments.of("ABB", LocalDate.of(2025, 1, 15), LocalDate.of(2025, 1, 16), 3L, BigDecimal.valueOf(40.48)),
                Arguments.of("ABB", LocalDate.of(2025, 1, 18), LocalDate.of(2025, 1, 18), 1L, BigDecimal.valueOf(0L)),
                Arguments.of("ABB", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 20), 4L, BigDecimal.valueOf(40.48)),
                Arguments.of("TBS", LocalDate.of(2025, 1, 15), LocalDate.of(2025, 1, 15), 0L, BigDecimal.valueOf(0L)),
                Arguments.of("TBS", LocalDate.of(2025, 1, 16), LocalDate.of(2025, 1, 16), 0L, BigDecimal.valueOf(0L)),
                Arguments.of("EKW", LocalDate.of(2025, 1, 15), LocalDate.of(2025, 1, 15), 0L, BigDecimal.valueOf(0L)),
                Arguments.of("EKW", LocalDate.of(2025, 1, 16), LocalDate.of(2025, 1, 16), 1L, BigDecimal.valueOf(1.33)),
                Arguments.of("ABB", LocalDate.of(2024, 1, 1), LocalDate.of(2025, 1, 20), 8L, BigDecimal.valueOf(80.96))
        );
    }
}
