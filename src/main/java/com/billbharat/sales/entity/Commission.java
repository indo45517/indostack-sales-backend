package com.billbharat.sales.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Commission records linked to sales and deliveries.
 */
@Entity
@Table(name = "commissions")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "sale_id", columnDefinition = "uuid")
    private UUID saleId;

    @Column(name = "delivery_id", columnDefinition = "uuid")
    private UUID deliveryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "commission_type", nullable = false)
    private CommissionType commissionType;

    @Column(name = "base_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseAmount;

    @Column(name = "commission_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal commissionRate;

    @Column(name = "commission_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal commissionAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.PENDING;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum CommissionType {
        SALE, DELIVERY, BONUS
    }

    public enum Status {
        PENDING, APPROVED, PAID
    }
}
