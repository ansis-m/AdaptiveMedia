package com.adaptivemedia.assignment.web;

import com.adaptivemedia.assignment.records.CommissionSummary;
import com.adaptivemedia.assignment.records.ConversionRate;
import com.adaptivemedia.assignment.records.ProductConversion;
import com.adaptivemedia.assignment.services.SalesAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sales-analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Sales Analytics", description = "APIs for sales tracking and conversion analytics")
public class SalesAnalyticsController {

    private final SalesAnalyticsService salesAnalyticsService;

    @Operation(
            summary = "Get conversion rate for landing page",
            description = "Returns conversion rate statistics for a specific landing page code within a date interval"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved conversion rate"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/conversion-rate")
    public ConversionRate getConversionRate(
            @Parameter(description = "Landing page tracking code", required = true, example = "ABB")
            @RequestParam String trackingCode,

            @Parameter(description = "Start date and time (ISO format)", required = true,
                    example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,

            @Parameter(description = "End date and time (ISO format)", required = true,
                    example = "2024-01-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        log.info("Getting conversion rate for landingPageCode: {}, dateRange: {} to {}",
                 trackingCode, fromDate, toDate);

        return salesAnalyticsService.getConversionRate(trackingCode, fromDate, toDate);
    }

    @Operation(
            summary = "Get total commission for landing page",
            description = "Returns total commission earned for a specific landing page code within a date interval"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved commission data"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/commission")
    public CommissionSummary getTotalCommission(
            @Parameter(description = "Landing page tracking code", required = true, example = "ABB")
            @RequestParam String trackingCode,

            @Parameter(description = "Start date and time (ISO format)", required = true,
                    example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,

            @Parameter(description = "End date and time (ISO format)", required = true,
                    example = "2024-01-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        log.info("Getting total commission for landingPageCode: {}, dateRange: {} to {}",
                 trackingCode, fromDate, toDate);

        return salesAnalyticsService.getTotalCommission(trackingCode, fromDate, toDate);
    }

    @Operation(
            summary = "Get product conversion rates",
            description = "Returns conversion rates for all products within a date interval, ordered by conversion rate"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved product conversion rates"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/product-conversions")
    public List<ProductConversion> getProductConversionRates(
            @Parameter(description = "Start date and time (ISO format)", required = true,
                    example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,

            @Parameter(description = "End date and time (ISO format)", required = true,
                    example = "2024-01-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        log.info("Getting product conversion rates for dateRange: {} to {}", fromDate, toDate);

        return salesAnalyticsService.getProductConversionRates(fromDate, toDate);
    }
}
