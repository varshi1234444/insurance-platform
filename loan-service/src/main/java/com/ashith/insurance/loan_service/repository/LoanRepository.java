package com.ashith.insurance.loan_service.repository;

import com.ashith.insurance.loan_service.entity.Loan;
import com.ashith.insurance.loan_service.entity.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByCustomerEmailOrderByCreatedAtDesc(String customerEmail);

    Optional<Loan> findByIdAndCustomerEmail(Long id, String customerEmail);

    Page<Loan> findByStatus(LoanStatus status, Pageable pageable);

    boolean existsByPolicyIdAndStatusIn(Long policyId, Collection<LoanStatus> statuses);
}