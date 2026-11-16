package com.ashith.insurance.claims_service.dto;

import com.ashith.insurance.claims_service.entity.Claim;
import com.ashith.insurance.claims_service.entity.ClaimStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record ClaimResponse(
        Long id,
        String customerEmail,
        Long policyId,
        String policyName,
        BigDecimal claimAmount,
        BigDecimal approvedAmount,
        LocalDate incidentDate,
        String description,
        ClaimStatus status,
        String reviewedBy,
        String reviewComment,
        Instant createdAt,
        Instant updatedAt) {

    public static ClaimResponse from(Claim c) {
        return new ClaimResponse(c.getId(), c.getCustomerEmail(), c.getPolicyId(), c.getPolicyName(),
                c.getClaimAmount(), c.getApprovedAmount(), c.getIncidentDate(), c.getDescription(),
                c.getStatus(), c.getReviewedBy(), c.getReviewComment(), c.getCreatedAt(), c.getUpdatedAt());
    }
}