package com.ashith.insurance.auth_service.controller;

import com.ashith.insurance.auth_service.dto.UserResponse;
import com.ashith.insurance.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/api/users/me")
    public Map<String, String> me(Authentication authentication) {
        return Map.of("email", authentication.getName(),
                "authorities", authentication.getAuthorities().toString());
    }

    @GetMapping("/api/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> allUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserResponse(u.getId(), u.getEmail(), u.getFullName(), u.getRole()))
                .toList();
    }
}
