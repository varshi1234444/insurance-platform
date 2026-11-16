package com.ashith.insurance.grievance_service.dto;

import java.math.BigDecimal;

public record ResolutionStatsResponse(
        String category,
        long resolvedCount,
        BigDecimal avgHours) {
}