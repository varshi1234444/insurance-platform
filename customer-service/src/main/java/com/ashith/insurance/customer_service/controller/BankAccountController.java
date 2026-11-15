package com.ashith.insurance.customer_service.controller;

import com.ashith.insurance.customer_service.dto.BankAccountRequest;
import com.ashith.insurance.customer_service.dto.BankAccountResponse;
import com.ashith.insurance.customer_service.service.BankAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bank-accounts")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @PostMapping
    public ResponseEntity<BankAccountResponse> add(Authentication authentication,
                                                   @Valid @RequestBody BankAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bankAccountService.add(authentication.getName(), request));
    }

    @GetMapping
    public List<BankAccountResponse> list(Authentication authentication) {
        return bankAccountService.list(authentication.getName());
    }

    @PutMapping("/{id}/primary")
    public BankAccountResponse makePrimary(Authentication authentication, @PathVariable Long id) {
        return bankAccountService.makePrimary(authentication.getName(), id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long id) {
        bankAccountService.delete(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
