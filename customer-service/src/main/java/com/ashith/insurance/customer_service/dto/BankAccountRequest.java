package com.ashith.insurance.customer_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BankAccountRequest(
        @NotBlank @Size(max = 100) String accountHolderName,
        @NotBlank @Size(max = 100) String bankName,
        @NotBlank @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "must be a valid IFSC code") String ifscCode,
        @NotBlank @Pattern(regexp = "^[0-9]{9,18}$", message = "must be 9 to 18 digits") String accountNumber,
        Boolean primaryAccount) {
}