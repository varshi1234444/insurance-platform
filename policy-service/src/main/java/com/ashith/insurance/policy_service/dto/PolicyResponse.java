package com.ashith.insurance.policy_service.dto;

import com.ashith.insurance.policy_service.entity.PolicyType;

import java.io.Serializable;
import java.math.BigDecimal;

public record PolicyResponse(
        Long id,
        String name,
        PolicyType type,
        String description,
        BigDecimal annualPremium,
        BigDecimal coverAmount,
        int termYears,
        boolean active) implements Serializable {
}