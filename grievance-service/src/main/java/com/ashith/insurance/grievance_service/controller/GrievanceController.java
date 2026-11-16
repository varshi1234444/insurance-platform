package com.ashith.insurance.grievance_service.controller;

import com.ashith.insurance.grievance_service.dto.CategoryStatusCountResponse;
import com.ashith.insurance.grievance_service.dto.CommentRequest;
import com.ashith.insurance.grievance_service.dto.CommentResponse;
import com.ashith.insurance.grievance_service.dto.CreateGrievanceRequest;
import com.ashith.insurance.grievance_service.dto.GrievanceResponse;
import com.ashith.insurance.grievance_service.dto.PageResponse;
import com.ashith.insurance.grievance_service.dto.ResolutionStatsResponse;
import com.ashith.insurance.grievance_service.dto.SummaryResponse;
import com.ashith.insurance.grievance_service.dto.UpdateStatusRequest;
import com.ashith.insurance.grievance_service.entity.GrievanceCategory;
import com.ashith.insurance.grievance_service.entity.GrievanceStatus;
import com.ashith.insurance.grievance_service.service.GrievanceService;
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
import java.util.Objects;

@RestController
@RequestMapping("/api/grievances")
@RequiredArgsConstructor
public class GrievanceController {

    private final GrievanceService grievanceService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<GrievanceResponse> create(Authentication authentication,
                                                    @Valid @RequestBody CreateGrievanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(grievanceService.create(authentication.getName(), request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<GrievanceResponse> mine(Authentication authentication) {
        return grievanceService.myGrievances(authentication.getName());
    }

    @GetMapping("/{id}")
    public GrievanceResponse get(@PathVariable Long id, Authentication authentication) {
        return grievanceService.getForUser(id, authentication.getName(), isStaff(authentication));
    }

    @GetMapping("/{id}/comments")
    public List<CommentResponse> comments(@PathVariable Long id, Authentication authentication) {
        return grievanceService.comments(id, authentication.getName(), isStaff(authentication));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long id,
                                                      Authentication authentication,
                                                      @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(grievanceService.addComment(id, authentication.getName(), isStaff(authentication), request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
    public PageResponse<GrievanceResponse> search(@RequestParam(required = false) GrievanceStatus status,
                                                  @RequestParam(required = false) GrievanceCategory category,
                                                  @RequestParam(defaultValue = "30") int days,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        int safeSize = Math.min(Math.max(size, 1), 50);
        int safeDays = Math.min(Math.max(days, 1), 365);
        return grievanceService.search(status, category, safeDays,
                PageRequest.of(Math.max(page, 0), safeSize, Sort.by("slaDeadline")));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
    public GrievanceResponse updateStatus(@PathVariable Long id,
                                          Authentication authentication,
                                          @Valid @RequestBody UpdateStatusRequest request) {
        return grievanceService.updateStatus(id, request.status(), authentication.getName());
    }

    // ----- Reports (admin only) -----

    @GetMapping("/reports/category-status")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CategoryStatusCountResponse> categoryStatus() {
        return grievanceService.categoryStatusCounts();
    }

    @GetMapping("/reports/resolution-time")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ResolutionStatsResponse> resolutionTime() {
        return grievanceService.resolutionStats();
    }

    @GetMapping("/reports/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public SummaryResponse summary(@RequestParam(defaultValue = "30") int days) {
        return grievanceService.summary(Math.clamp(days, 1, 365));
    }

    private boolean isStaff(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN") || a.getAuthority().equals("ROLE_AGENT"));
    }
}
