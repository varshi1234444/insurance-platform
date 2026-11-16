package com.ashith.insurance.loan_service.strategy;

import com.ashith.insurance.loan_service.entity.LoanType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class EducationLoanStrategy implements LoanTermsStrategy {

    @Override
    public LoanType type() {
        return LoanType.EDUCATION;
    }

    @Override
    public BigDecimal annualInterestRate() {
        return new BigDecimal("8.00");
    }

    @Override
    public int maxTenureMonths() {
        return 84;
    }

    @Override
    public BigDecimal maxPercentOfCover() {
        return new BigDecimal("15");
    }
}