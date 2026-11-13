package com.ashith.insurance.auth_service.dto;

import com.ashith.insurance.auth_service.entity.Role;

public record UserResponse(Long id, String email, String fullName, Role role) {
}