package com.billbharat.sales.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Beat plan for scheduling sales executive visits to a territory.
 */
@Entity
@Table(name = "beat_plans")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeatPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "beat_name", nullable = false, length = 200)
    private String beatName;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "territory_id", columnDefinition = "uuid")
    private UUID territoryId;

    @Column(name = "executive_id", nullable = false, columnDefinition = "uuid")
    private UUID executiveId;

    @Column(name = "team_lead_id", nullable = false, columnDefinition = "uuid")
    private UUID teamLeadId;

    @Column(name = "visit_target")
    @Builder.Default
    private Integer visitTarget = 0;

    @Column(name = "sales_target", precision = 15, scale = 2)
    private BigDecimal salesTarget;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.NOT_STARTED;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Status {
        NOT_STARTED, IN_PROGRESS, COMPLETED, CANCELLED
    }
}
