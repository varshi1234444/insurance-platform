package com.ashith.insurance.customer_service.dto;

import com.ashith.insurance.customer_service.entity.BankAccount;

import java.time.Instant;

public record BankAccountResponse(
        Long id,
        String accountHolderName,
        String bankName,
        String ifscCode,
        String maskedAccountNumber,
        boolean primaryAccount,
        Instant createdAt) {

    public static BankAccountResponse from(BankAccount a) {
        return new BankAccountResponse(a.getId(), a.getAccountHolderName(), a.getBankName(),
                a.getIfscCode(), "XXXXXXXX" + a.getAccountNumberLast4(),
                a.isPrimaryAccount(), a.getCreatedAt());
    }
}