package com.ashith.insurance.grievance_service.dto;

import com.ashith.insurance.grievance_service.entity.Grievance;
import com.ashith.insurance.grievance_service.entity.GrievanceCategory;
import com.ashith.insurance.grievance_service.entity.GrievancePriority;
import com.ashith.insurance.grievance_service.entity.GrievanceStatus;

import java.time.Instant;

public record GrievanceResponse(
        Long id,
        String customerEmail,
        GrievanceCategory category,
        GrievancePriority priority,
        String subject,
        String description,
        GrievanceStatus status,
        String assignedTo,
        Instant slaDeadline,
        boolean escalated,
        Instant escalatedAt,
        Instant resolvedAt,
        Instant createdAt,
        Instant updatedAt) {

    public static GrievanceResponse from(Grievance g) {
        return new GrievanceResponse(g.getId(), g.getCustomerEmail(), g.getCategory(), g.getPriority(),
                g.getSubject(), g.getDescription(), g.getStatus(), g.getAssignedTo(), g.getSlaDeadline(),
                g.isEscalated(), g.getEscalatedAt(), g.getResolvedAt(), g.getCreatedAt(), g.getUpdatedAt());
    }
}
