package com.ashith.insurance.loan_service.strategy;

import com.ashith.insurance.loan_service.entity.LoanType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class EmergencyLoanStrategy implements LoanTermsStrategy {

    @Override
    public LoanType type() {
        return LoanType.EMERGENCY;
    }

    @Override
    public BigDecimal annualInterestRate() {
        return new BigDecimal("11.50");
    }

    @Override
    public int maxTenureMonths() {
        return 24;
    }

    @Override
    public BigDecimal maxPercentOfCover() {
        return new BigDecimal("5");
    }
}
