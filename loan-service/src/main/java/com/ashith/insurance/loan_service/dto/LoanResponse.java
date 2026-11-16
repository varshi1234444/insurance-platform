package com.ashith.insurance.loan_service.dto;

import com.ashith.insurance.loan_service.entity.Loan;
import com.ashith.insurance.loan_service.entity.LoanStatus;
import com.ashith.insurance.loan_service.entity.LoanType;

import java.math.BigDecimal;
import java.time.Instant;

public record LoanResponse(
        Long id,
        String customerEmail,
        Long policyId,
        String policyName,
        LoanType loanType,
        BigDecimal principal,
        int tenureMonths,
        BigDecimal annualInterestRate,
        BigDecimal emi,
        BigDecimal totalPayable,
        LoanStatus status,
        String reviewedBy,
        String reviewComment,
        String disbursedToAccount,
        Instant disbursedAt,
        Instant createdAt) {

    public static LoanResponse from(Loan l) {
        return new LoanResponse(l.getId(), l.getCustomerEmail(), l.getPolicyId(), l.getPolicyName(),
                l.getLoanType(), l.getPrincipal(), l.getTenureMonths(), l.getAnnualInterestRate(),
                l.getEmi(), l.getTotalPayable(), l.getStatus(), l.getReviewedBy(), l.getReviewComment(),
                l.getDisbursedToAccount(), l.getDisbursedAt(), l.getCreatedAt());
    }
}