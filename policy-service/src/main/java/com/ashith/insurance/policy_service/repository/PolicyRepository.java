package com.ashith.insurance.policy_service.repository;

import com.ashith.insurance.policy_service.entity.Policy;
import com.ashith.insurance.policy_service.entity.PolicyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    Page<Policy> findByActiveTrue(Pageable pageable);

    Page<Policy> findByTypeAndActiveTrue(PolicyType type, Pageable pageable);

    Optional<Policy> findByIdAndActiveTrue(Long id);

    boolean existsByNameIgnoreCase(String name);
}