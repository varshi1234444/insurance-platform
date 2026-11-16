package com.ashith.insurance.grievance_service.dto;

import com.ashith.insurance.grievance_service.entity.GrievanceCategory;
import com.ashith.insurance.grievance_service.entity.GrievancePriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateGrievanceRequest(
        @NotNull GrievanceCategory category,
        @NotNull GrievancePriority priority,
        @NotBlank @Size(max = 150) String subject,
        @NotBlank @Size(max = 2000) String description) {
}