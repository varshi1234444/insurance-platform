package com.ashith.insurance.customer_service.repository;

import com.ashith.insurance.customer_service.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("select c from Customer c where lower(c.fullName) like lower(concat('%', :name, '%'))")
    Page<Customer> searchByName(@Param("name") String name, Pageable pageable);
}