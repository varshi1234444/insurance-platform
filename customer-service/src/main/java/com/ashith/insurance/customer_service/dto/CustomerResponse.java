package com.ashith.insurance.customer_service.dto;

import com.ashith.insurance.customer_service.entity.Customer;

import java.time.LocalDate;

public record CustomerResponse(
        Long id,
        String email,
        String fullName,
        String phone,
        LocalDate dateOfBirth,
        String address,
        String nomineeName,
        String nomineeRelationship,
        Long version) {

    public static CustomerResponse from(Customer c) {
        return new CustomerResponse(c.getId(), c.getEmail(), c.getFullName(), c.getPhone(),
                c.getDateOfBirth(), c.getAddress(), c.getNomineeName(),
                c.getNomineeRelationship(), c.getVersion());
    }
}