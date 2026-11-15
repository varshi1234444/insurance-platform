package com.ashith.insurance.customer_service.dto;

import com.ashith.insurance.customer_service.entity.NomineeChangeRequest;
import com.ashith.insurance.customer_service.entity.NomineeRequestStatus;

import java.time.Instant;

public record NomineeChangeResponse(
        Long id,
        Long customerId,
        String newNomineeName,
        String newNomineeRelationship,
        String reason,
        NomineeRequestStatus status,
        String reviewedBy,
        String reviewComment,
        Instant createdAt,
        Instant reviewedAt) {

    public static NomineeChangeResponse from(NomineeChangeRequest r) {
        return new NomineeChangeResponse(r.getId(), r.getCustomer().getId(), r.getNewNomineeName(),
                r.getNewNomineeRelationship(), r.getReason(), r.getStatus(), r.getReviewedBy(),
                r.getReviewComment(), r.getCreatedAt(), r.getReviewedAt());
    }
}
