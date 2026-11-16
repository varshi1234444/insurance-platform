package com.ashith.insurance.grievance_service.repository;

import com.ashith.insurance.grievance_service.entity.GrievanceComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrievanceCommentRepository extends JpaRepository<GrievanceComment, Long> {

    List<GrievanceComment> findByGrievanceIdOrderByCreatedAtAsc(Long grievanceId);
}
