package com.ashith.insurance.grievance_service.dto;

import com.ashith.insurance.grievance_service.entity.GrievanceStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(
        @NotNull GrievanceStatus status) {
}
