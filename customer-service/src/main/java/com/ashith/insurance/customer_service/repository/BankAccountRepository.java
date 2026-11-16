package com.ashith.insurance.customer_service.repository;

import com.ashith.insurance.customer_service.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    List<BankAccount> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    Optional<BankAccount> findByIdAndCustomerId(Long id, Long customerId);
    Optional<BankAccount> findByCustomerIdAndPrimaryAccountTrue(Long customerId);

    boolean existsByCustomerIdAndAccountNumberHash(Long customerId, String accountNumberHash);

    long countByCustomerId(Long customerId);

    @Modifying(clearAutomatically = true)
    @Query("update BankAccount b set b.primaryAccount = false where b.customer.id = :customerId")
    int clearPrimary(@Param("customerId") Long customerId);
}