package com.ashith.insurance.grievance_service.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EscalationJob {

    private static final Logger log = LoggerFactory.getLogger(EscalationJob.class);

    private final GrievanceService grievanceService;

    @Scheduled(fixedDelayString = "${grievance.escalation.interval-ms:60000}")
    public void escalateOverdueGrievances() {
        int escalated = grievanceService.escalateOverdue();
        if (escalated > 0) {
            log.info("Escalated {} overdue grievance(s)", escalated);
        }
    }
}
