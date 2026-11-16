package com.ashith.insurance.loan_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "loans")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private Long policyId;

    @Column(nullable = false)
    private String policyName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanType loanType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal principal;

    @Column(nullable = false)
    private int tenureMonths;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal annualInterestRate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal emi;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalPayable;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LoanStatus status = LoanStatus.APPLIED;

    private String reviewedBy;

    @Column(length = 500)
    private String reviewComment;

    private String disbursedToAccount;

    private Instant disbursedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @Version
    private Long version;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}