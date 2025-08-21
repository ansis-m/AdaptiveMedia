package com.adaptivemedia.assignment.records;

public record ProductConversion(
        String product,
        Integer totalVisits,
        Integer totalSales,
        Double conversionRate
) {}