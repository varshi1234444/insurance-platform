package com.ashith.insurance.policy_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyType type;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal annualPremium;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal coverAmount;

    @Column(nullable = false)
    private int termYears;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @Version
    private Long version;
}
