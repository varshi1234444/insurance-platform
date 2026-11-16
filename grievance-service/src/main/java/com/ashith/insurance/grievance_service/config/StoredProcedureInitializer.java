package com.ashith.insurance.grievance_service.config;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoredProcedureInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String @NonNull ... args) {
        jdbcTemplate.execute("DROP PROCEDURE IF EXISTS grievance_summary");
        jdbcTemplate.execute("""
                CREATE PROCEDURE grievance_summary(IN p_days INT)
                BEGIN
                    SELECT COUNT(*) AS total,
                           CAST(COALESCE(SUM(status = 'OPEN'), 0) AS SIGNED) AS openCount,
                           CAST(COALESCE(SUM(status = 'IN_PROGRESS'), 0) AS SIGNED) AS inProgressCount,
                           CAST(COALESCE(SUM(status IN ('RESOLVED', 'CLOSED')), 0) AS SIGNED) AS resolvedCount,
                           CAST(COALESCE(SUM(escalated = 1), 0) AS SIGNED) AS escalatedCount
                    FROM grievances
                    WHERE created_at >= UTC_TIMESTAMP() - INTERVAL p_days DAY;
                END
                """);
    }
}
