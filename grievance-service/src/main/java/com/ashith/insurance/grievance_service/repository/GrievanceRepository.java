package com.ashith.insurance.grievance_service.repository;

import com.ashith.insurance.grievance_service.entity.Grievance;
import com.ashith.insurance.grievance_service.entity.GrievanceCategory;
import com.ashith.insurance.grievance_service.entity.GrievanceStatus;
import com.ashith.insurance.grievance_service.projection.CategoryStatusCount;
import com.ashith.insurance.grievance_service.projection.GrievanceSummary;
import com.ashith.insurance.grievance_service.projection.ResolutionStats;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GrievanceRepository extends JpaRepository<Grievance, Long> {

    List<Grievance> findByCustomerEmailOrderByCreatedAtDesc(String customerEmail);

    Optional<Grievance> findByIdAndCustomerEmail(Long id, String customerEmail);

    // JPQL search with optional filters
    @Query("""
            select g from Grievance g
            where (:status is null or g.status = :status)
              and (:category is null or g.category = :category)
              and g.createdAt >= :from
            """)
    Page<Grievance> search(@Param("status") GrievanceStatus status,
                           @Param("category") GrievanceCategory category,
                           @Param("from") Instant from,
                           Pageable pageable);

    // Bulk JPQL update: one SQL UPDATE for every overdue ticket.
    // A bulk update skips @PreUpdate, so updatedAt is set explicitly here.
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Grievance g
               set g.escalated = true, g.escalatedAt = :now, g.updatedAt = :now
             where g.escalated = false
               and g.status in :statuses
               and g.slaDeadline < :now
            """)
    int escalateOverdue(@Param("now") Instant now,
                        @Param("statuses") Collection<GrievanceStatus> statuses);

    // JPQL aggregate with GROUP BY
    @Query("""
            select g.category as category, g.status as status, count(g) as total
            from Grievance g
            group by g.category, g.status
            order by g.category, g.status
            """)
    List<CategoryStatusCount> countByCategoryAndStatus();

    // Native SQL using MySQL date functions
    @Query(value = """
            select category as category,
                   count(*) as resolvedCount,
                   round(avg(timestampdiff(MINUTE, created_at, resolved_at)) / 60, 2) as avgHours
            from grievances
            where resolved_at is not null
            group by category
            order by category
            """, nativeQuery = true)
    List<ResolutionStats> resolutionStats();

    // Stored procedure call (the procedure is created at startup in the next step)
    @Query(value = "CALL grievance_summary(:days)", nativeQuery = true)
    List<GrievanceSummary> summaryForLastDays(@Param("days") int days);
}