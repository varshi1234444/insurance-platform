package com.ashith.insurance.grievance_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "grievances", indexes = {
        @Index(name = "idx_grievance_status_deadline", columnList = "status, slaDeadline"),
        @Index(name = "idx_grievance_customer", columnList = "customerEmail")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grievance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GrievanceCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GrievancePriority priority;

    @Column(nullable = false, length = 150)
    private String subject;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private GrievanceStatus status = GrievanceStatus.OPEN;

    private String assignedTo;

    @Column(nullable = false)
    private Instant slaDeadline;

    @Column(nullable = false)
    @Builder.Default
    private boolean escalated = false;

    private Instant escalatedAt;

    private Instant resolvedAt;

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
