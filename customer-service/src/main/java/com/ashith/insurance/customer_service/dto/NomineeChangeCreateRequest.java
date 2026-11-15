package com.ashith.insurance.customer_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NomineeChangeCreateRequest(
        @NotBlank @Size(max = 100) String newNomineeName,
        @NotBlank @Size(max = 50) String newNomineeRelationship,
        @Size(max = 500) String reason) {
}