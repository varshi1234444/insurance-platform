package com.ashith.insurance.grievance_service.projection;

public interface GrievanceSummary {

    long getTotal();

    long getOpenCount();

    long getInProgressCount();

    long getResolvedCount();

    long getEscalatedCount();
}
