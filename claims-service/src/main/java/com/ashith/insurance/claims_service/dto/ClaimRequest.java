package com.ashith.insurance.claims_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ClaimRequest(
        @NotNull Long policyId,
        @NotNull @Positive BigDecimal claimAmount,
        @NotNull @PastOrPresent LocalDate incidentDate,
        @NotBlank @Size(max = 1000) String description) {
}