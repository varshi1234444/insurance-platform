package com.ashith.insurance.loan_service.strategy;

import com.ashith.insurance.loan_service.entity.LoanType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StandardLoanStrategy implements LoanTermsStrategy {

    @Override
    public LoanType type() {
        return LoanType.STANDARD;
    }

    @Override
    public BigDecimal annualInterestRate() {
        return new BigDecimal("9.00");
    }

    @Override
    public int maxTenureMonths() {
        return 60;
    }

    @Override
    public BigDecimal maxPercentOfCover() {
        return new BigDecimal("10");
    }
}