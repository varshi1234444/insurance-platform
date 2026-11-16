package com.ashith.insurance.loan_service.strategy;

import com.ashith.insurance.loan_service.entity.LoanType;

import java.math.BigDecimal;

public interface LoanTermsStrategy {

    LoanType type();

    BigDecimal annualInterestRate();

    int maxTenureMonths();

    BigDecimal maxPercentOfCover();
}