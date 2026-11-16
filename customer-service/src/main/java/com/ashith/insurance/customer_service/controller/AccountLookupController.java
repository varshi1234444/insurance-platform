package com.ashith.insurance.customer_service.controller;

import com.ashith.insurance.customer_service.dto.BankAccountResponse;
import com.ashith.insurance.customer_service.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers/primary-account")
@RequiredArgsConstructor
public class AccountLookupController {

    private final BankAccountService bankAccountService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public BankAccountResponse primaryAccount(@RequestParam String email) {
        return bankAccountService.primaryAccountOf(email);
    }
}