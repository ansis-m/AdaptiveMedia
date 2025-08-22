package com.adaptivemedia.assignment.records;

public record ProductConversion(
        String product,
        Integer visitActions,
        Integer purchaseActions,
        Double conversionRate
) {}