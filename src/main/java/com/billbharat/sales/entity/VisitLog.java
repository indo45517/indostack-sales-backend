package com.billbharat.sales.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Logs shop/merchant visits by sales executives.
 */
@Entity
@Table(name = "visit_logs")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "merchant_id", columnDefinition = "uuid")
    private UUID merchantId;

    @Column(name = "shop_name")
    private String shopName;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "visit_time")
    private LocalDateTime visitTime;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.VISITED;

    @Column(name = "purpose", length = 100)
    private String purpose;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum Status {
        VISITED, FOLLOW_UP, CONVERTED, NOT_INTERESTED
    }
}
