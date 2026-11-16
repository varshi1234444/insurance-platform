package com.ashith.insurance.loan_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoanDecisionRequest(
        @NotNull Boolean approved,
        @Size(max = 500) String comment) {
}