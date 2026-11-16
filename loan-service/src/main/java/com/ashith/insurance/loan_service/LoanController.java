package com.ashith.insurance.loan_service.controller;

import com.ashith.insurance.loan_service.dto.LoanApplyRequest;
import com.ashith.insurance.loan_service.dto.LoanDecisionRequest;
import com.ashith.insurance.loan_service.dto.LoanResponse;
import com.ashith.insurance.loan_service.dto.PageResponse;
import com.ashith.insurance.loan_service.entity.LoanStatus;
import com.ashith.insurance.loan_service.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<LoanResponse> apply(Authentication authentication,
                                              @RequestHeader("Authorization") String authorization,
                                              @Valid @RequestBody LoanApplyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(loanService.apply(authentication.getName(), request, authorization));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<LoanResponse> mine(Authentication authentication) {
        return loanService.myLoans(authentication.getName());
    }

    @GetMapping("/me/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public LoanResponse mineById(Authentication authentication, @PathVariable Long id) {
        return loanService.getMine(authentication.getName(), id);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<LoanResponse> byStatus(@RequestParam(defaultValue = "APPLIED") LoanStatus status,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        int safeSize = Math.min(Math.max(size, 1), 50);
        return loanService.byStatus(status,
                PageRequest.of(Math.max(page, 0), safeSize, Sort.by("createdAt")));
    }

    @PutMapping("/{id}/decision")
    @PreAuthorize("hasRole('ADMIN')")
    public LoanResponse decide(@PathVariable Long id,
                               Authentication authentication,
                               @Valid @RequestBody LoanDecisionRequest decision) {
        return loanService.decide(id, authentication.getName(), decision);
    }
}