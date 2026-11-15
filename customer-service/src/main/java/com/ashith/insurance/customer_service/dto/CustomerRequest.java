package com.ashith.insurance.customer_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CustomerRequest(
        @NotBlank @Size(max = 100) String fullName,
        @Pattern(regexp = "^[6-9][0-9]{9}$", message = "must be a valid 10-digit Indian mobile number") String phone,
        @Past LocalDate dateOfBirth,
        @Size(max = 500) String address) {
}