package com.ashith.insurance.claims_service.repository;

import com.ashith.insurance.claims_service.entity.Claim;
import com.ashith.insurance.claims_service.entity.ClaimStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByCustomerEmailOrderByCreatedAtDesc(String customerEmail);

    Optional<Claim> findByIdAndCustomerEmail(Long id, String customerEmail);

    Page<Claim> findByStatus(ClaimStatus status, Pageable pageable);
}