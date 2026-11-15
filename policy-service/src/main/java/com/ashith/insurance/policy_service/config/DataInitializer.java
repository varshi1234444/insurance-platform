package com.ashith.insurance.policy_service.config;

import com.ashith.insurance.policy_service.entity.Policy;
import com.ashith.insurance.policy_service.entity.PolicyType;
import com.ashith.insurance.policy_service.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PolicyRepository policyRepository;

    @Override
    public void run(String @NonNull ... args) {
        if (policyRepository.count() > 0) {
            return;
        }
        policyRepository.saveAll(List.of(
                policy("Term Life 1 Crore", PolicyType.LIFE, "Pure term cover for the family", 12000, 10000000, 20),
                policy("Family Health Shield", PolicyType.HEALTH, "Floater cover for four members", 18000, 1000000, 1),
                policy("Comprehensive Motor Cover", PolicyType.MOTOR, "Own damage and third party", 9500, 500000, 1),
                policy("Home Secure", PolicyType.HOME, "Fire, theft and natural disasters", 6000, 2500000, 5),
                policy("Senior Citizen Health", PolicyType.HEALTH, "Health cover for age 60 and above", 25000, 800000, 1),
                policy("Child Education Plan", PolicyType.LIFE, "Savings and cover for education", 15000, 2000000, 15)));
    }

    private Policy policy(String name, PolicyType type, String description,
                          int premium, int cover, int termYears) {
        return Policy.builder()
                .name(name)
                .type(type)
                .description(description)
                .annualPremium(BigDecimal.valueOf(premium))
                .coverAmount(BigDecimal.valueOf(cover))
                .termYears(termYears)
                .build();
    }
}
