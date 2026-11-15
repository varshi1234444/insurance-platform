package com.ashith.insurance.customer_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "nominee_change_requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NomineeChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(nullable = false)
    private String newNomineeName;

    @Column(nullable = false)
    private String newNomineeRelationship;

    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private NomineeRequestStatus status = NomineeRequestStatus.PENDING;

    private String reviewedBy;

    @Column(length = 500)
    private String reviewComment;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant reviewedAt;

    @Version
    private Long version;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }
}
