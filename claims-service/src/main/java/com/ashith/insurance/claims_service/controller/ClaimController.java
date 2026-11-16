package com.ashith.insurance.claims_service.controller;

import com.ashith.insurance.claims_service.dto.ClaimRequest;
import com.ashith.insurance.claims_service.dto.ClaimResponse;
import com.ashith.insurance.claims_service.dto.PageResponse;
import com.ashith.insurance.claims_service.dto.ReviewClaimRequest;
import com.ashith.insurance.claims_service.entity.ClaimStatus;
import com.ashith.insurance.claims_service.service.ClaimService;
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
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ClaimResponse> submit(Authentication authentication,
                                                @RequestHeader("Authorization") String authorization,
                                                @Valid @RequestBody ClaimRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(claimService.submit(authentication.getName(), request, authorization));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<ClaimResponse> mine(Authentication authentication) {
        return claimService.myClaims(authentication.getName());
    }

    @GetMapping("/me/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ClaimResponse mineById(Authentication authentication, @PathVariable Long id) {
        return claimService.getMine(authentication.getName(), id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CLAIMS_OFFICER','ADMIN')")
    public PageResponse<ClaimResponse> byStatus(@RequestParam(defaultValue = "SUBMITTED") ClaimStatus status,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        int safeSize = Math.min(Math.max(size, 1), 50);
        return claimService.byStatus(status,
                PageRequest.of(Math.max(page, 0), safeSize, Sort.by("createdAt")));
    }

    @PutMapping("/{id}/start-review")
    @PreAuthorize("hasRole('CLAIMS_OFFICER')")
    public ClaimResponse startReview(@PathVariable Long id, Authentication authentication) {
        return claimService.startReview(id, authentication.getName());
    }

    @PutMapping("/{id}/decision")
    @PreAuthorize("hasRole('CLAIMS_OFFICER')")
    public ClaimResponse decide(@PathVariable Long id,
                                Authentication authentication,
                                @Valid @RequestBody ReviewClaimRequest review) {
        return claimService.decide(id, authentication.getName(), review);
    }
}