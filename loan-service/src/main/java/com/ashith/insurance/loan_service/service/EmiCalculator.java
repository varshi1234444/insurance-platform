package com.ashith.insurance.loan_service.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Component
public class EmiCalculator {

    private static final MathContext MC = MathContext.DECIMAL64;

    /** EMI = P * r * (1+r)^n / ((1+r)^n - 1), where r is the monthly rate. */
    public BigDecimal emi(BigDecimal principal, BigDecimal annualRatePercent, int months) {
        BigDecimal monthlyRate = annualRatePercent.divide(BigDecimal.valueOf(1200), MC);
        if (monthlyRate.signum() == 0) {
            return principal.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
        }
        BigDecimal factor = BigDecimal.ONE.add(monthlyRate).pow(months, MC);
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(factor);
        BigDecimal denominator = factor.subtract(BigDecimal.ONE);
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }
}