package com.adaptivemedia.assignment.records;

public record ConversionRate(
        String trackingCode,
        Long totalVisits,
        Long totalSales,
        Double conversionRate
) {}
