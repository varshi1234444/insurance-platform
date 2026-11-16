package com.ashith.insurance.claims_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ReviewClaimRequest(
        @NotNull Boolean approved,
        @Positive BigDecimal approvedAmount,
        @Size(max = 500) String comment) {
}
