package com.ashith.insurance.loan_service.dto;

import com.ashith.insurance.loan_service.entity.LoanType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LoanApplyRequest(
        @NotNull Long policyId,
        @NotNull LoanType loanType,
        @NotNull @DecimalMin(value = "1000", message = "must be at least 1000") BigDecimal principal,
        @Min(3) @Max(84) int tenureMonths) {
}
