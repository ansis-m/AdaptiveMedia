package com.adaptivemedia.assignment.records;

import java.math.BigDecimal;

public record CommissionSummary(
        String trackingCode,
        BigDecimal totalCommission,
        Long purchaseActions
) {}