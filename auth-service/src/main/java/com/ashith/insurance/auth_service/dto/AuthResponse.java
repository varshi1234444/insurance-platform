package com.ashith.insurance.auth_service.dto;

public record AuthResponse(String accessToken, String tokenType, long expiresInMs, String role) {
}