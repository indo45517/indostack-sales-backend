package com.billbharat.sales.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Merchant/shop details.
 */
@Entity
@Table(name = "merchants")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "shop_name", nullable = false, length = 200)
    private String shopName;

    @Column(name = "owner_name", length = 100)
    private String ownerName;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column
    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "shop_type")
    private ShopType shopType;

    @Column(name = "assigned_executive_id", columnDefinition = "uuid")
    private UUID assignedExecutiveId;

    @Column(name = "territory_id", columnDefinition = "uuid")
    private UUID territoryId;

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    @Column(name = "gst_number", length = 20)
    private String gstNumber;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ShopType {
        RETAIL, WHOLESALE, DISTRIBUTOR, RESTAURANT, OTHER
    }
}
