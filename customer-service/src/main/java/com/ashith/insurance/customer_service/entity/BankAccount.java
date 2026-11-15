package com.ashith.insurance.customer_service.entity;

import com.ashith.insurance.customer_service.config.AesAttributeConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "bank_accounts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "account_number_hash"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(nullable = false)
    private String accountHolderName;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false, length = 11)
    private String ifscCode;

    @Convert(converter = AesAttributeConverter.class)
    @Column(nullable = false, length = 255)
    private String accountNumber;

    @Column(nullable = false, length = 4)
    private String accountNumberLast4;

    @Column(nullable = false, length = 64)
    private String accountNumberHash;

    @Column(nullable = false)
    private boolean primaryAccount;

    @Version
    private Long version;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }
}