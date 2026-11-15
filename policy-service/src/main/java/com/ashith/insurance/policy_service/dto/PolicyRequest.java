package com.ashith.insurance.policy_service.dto;

import com.ashith.insurance.policy_service.entity.PolicyType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PolicyRequest(
        @NotBlank String name,
        @NotNull PolicyType type,
        @Size(max = 1000) String description,
        @NotNull @Positive BigDecimal annualPremium,
        @NotNull @Positive BigDecimal coverAmount,
        @Min(1) @Max(50) int termYears) {
}