package com.ashith.insurance.policy_service.controller;

import com.ashith.insurance.policy_service.dto.PageResponse;
import com.ashith.insurance.policy_service.dto.PolicyRequest;
import com.ashith.insurance.policy_service.dto.PolicyResponse;
import com.ashith.insurance.policy_service.entity.PolicyType;
import com.ashith.insurance.policy_service.service.PolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @GetMapping
    public PageResponse<PolicyResponse> list(@RequestParam(required = false) PolicyType type,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        int safeSize = Math.min(Math.max(size, 1), 50);
        Pageable pageable = PageRequest.of(Math.max(page, 0), safeSize, Sort.by("name"));
        return policyService.list(type, pageable);
    }

    @GetMapping("/{id}")
    public PolicyResponse get(@PathVariable Long id) {
        return policyService.get(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PolicyResponse> create(@Valid @RequestBody PolicyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(policyService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PolicyResponse update(@PathVariable Long id, @Valid @RequestBody PolicyRequest request) {
        return policyService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        policyService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
