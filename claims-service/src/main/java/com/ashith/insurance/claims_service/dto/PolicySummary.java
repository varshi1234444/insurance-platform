package com.ashith.insurance.claims_service.dto;

import java.math.BigDecimal;

public record PolicySummary(
        Long id,
        String name,
        String type,
        String description,
        BigDecimal annualPremium,
        BigDecimal coverAmount,
        int termYears,
        boolean active) {
}