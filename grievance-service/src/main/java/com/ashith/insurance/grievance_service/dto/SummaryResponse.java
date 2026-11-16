package com.ashith.insurance.grievance_service.dto;

public record SummaryResponse(
        int days,
        long total,
        long open,
        long inProgress,
        long resolved,
        long escalated) {
}
