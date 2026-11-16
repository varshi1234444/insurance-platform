package com.ashith.insurance.grievance_service.dto;

import com.ashith.insurance.grievance_service.entity.GrievanceCategory;
import com.ashith.insurance.grievance_service.entity.GrievanceStatus;

public record CategoryStatusCountResponse(
        GrievanceCategory category,
        GrievanceStatus status,
        long total) {
}
