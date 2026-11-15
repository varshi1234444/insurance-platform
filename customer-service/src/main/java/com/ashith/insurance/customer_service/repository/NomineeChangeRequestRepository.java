package com.ashith.insurance.customer_service.repository;

import com.ashith.insurance.customer_service.entity.NomineeChangeRequest;
import com.ashith.insurance.customer_service.entity.NomineeRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NomineeChangeRequestRepository extends JpaRepository<NomineeChangeRequest, Long> {

    List<NomineeChangeRequest> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    Page<NomineeChangeRequest> findByStatus(NomineeRequestStatus status, Pageable pageable);

    boolean existsByCustomerIdAndStatus(Long customerId, NomineeRequestStatus status);
}