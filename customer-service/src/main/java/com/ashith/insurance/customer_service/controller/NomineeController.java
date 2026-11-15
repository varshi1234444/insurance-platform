package com.ashith.insurance.customer_service.controller;

import com.ashith.insurance.customer_service.dto.NomineeChangeCreateRequest;
import com.ashith.insurance.customer_service.dto.NomineeChangeResponse;
import com.ashith.insurance.customer_service.dto.PageResponse;
import com.ashith.insurance.customer_service.dto.ReviewRequest;
import com.ashith.insurance.customer_service.service.NomineeService;
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
@RequestMapping("/api/nominee-requests")
@RequiredArgsConstructor
public class NomineeController {

    private final NomineeService nomineeService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<NomineeChangeResponse> submit(Authentication authentication,
                                                        @Valid @RequestBody NomineeChangeCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nomineeService.submit(authentication.getName(), request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<NomineeChangeResponse> mine(Authentication authentication) {
        return nomineeService.myRequests(authentication.getName());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
    public PageResponse<NomineeChangeResponse> pending(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        int safeSize = Math.clamp(size, 1, 50);
        return nomineeService.pending(
                PageRequest.of(Math.max(page, 0), safeSize, Sort.by("createdAt")));
    }

    @PutMapping("/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public NomineeChangeResponse review(@PathVariable Long id,
                                        Authentication authentication,
                                        @Valid @RequestBody ReviewRequest review) {
        return nomineeService.review(id, authentication.getName(), review);
    }
}
