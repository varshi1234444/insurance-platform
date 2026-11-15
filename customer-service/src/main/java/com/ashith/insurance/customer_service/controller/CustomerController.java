package com.ashith.insurance.customer_service.controller;

import com.ashith.insurance.customer_service.dto.CustomerRequest;
import com.ashith.insurance.customer_service.dto.CustomerResponse;
import com.ashith.insurance.customer_service.dto.PageResponse;
import com.ashith.insurance.customer_service.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/me")
    public CustomerResponse me(Authentication authentication) {
        return customerService.getByEmail(authentication.getName());
    }

    @PostMapping("/me")
    public ResponseEntity<CustomerResponse> create(Authentication authentication,
                                                   @Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.create(authentication.getName(), request));
    }

    @PutMapping("/me")
    public CustomerResponse update(Authentication authentication,
                                   @Valid @RequestBody CustomerRequest request) {
        return customerService.update(authentication.getName(), request);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
    public PageResponse<CustomerResponse> search(@RequestParam String name,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        int safeSize = Math.clamp(size, 1, 50);
        return customerService.search(name,
                PageRequest.of(Math.max(page, 0), safeSize, Sort.by("fullName")));
    }
}