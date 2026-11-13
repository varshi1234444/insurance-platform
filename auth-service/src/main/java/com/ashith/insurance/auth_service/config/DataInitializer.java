package com.ashith.insurance.auth_service.config;

import com.ashith.insurance.auth_service.entity.Role;
import com.ashith.insurance.auth_service.entity.User;
import com.ashith.insurance.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String @NonNull ... args) {
        createIfMissing("admin@insurance.com", "Admin@12345", "System Admin", Role.ADMIN);
        createIfMissing("agent@insurance.com", "Agent@12345", "Test Agent", Role.AGENT);
        createIfMissing("officer@insurance.com", "Officer@12345", "Test Officer", Role.CLAIMS_OFFICER);
    }

    private void createIfMissing(String email, String rawPassword, String fullName, Role role) {
        if (!userRepository.existsByEmail(email)) {
            userRepository.save(User.builder()
                    .email(email)
                    .password(passwordEncoder.encode(rawPassword))
                    .fullName(fullName)
                    .role(role)
                    .build());
        }
    }
}