package com.adaptivemedia.assignment.records;

public record ConversionRate(
        String trackingCode,
        Long visitActions,
        Long purchaseAction,
        Double conversionRate
) {}
