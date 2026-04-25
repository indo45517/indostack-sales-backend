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
 * Geographic territory assignment for sales executives.
 */
@Entity
@Table(name = "territories")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Territory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "assigned_to", columnDefinition = "uuid")
    private UUID assignedTo;

    @Column(name = "center_latitude", precision = 10, scale = 7)
    private BigDecimal centerLatitude;

    @Column(name = "center_longitude", precision = 10, scale = 7)
    private BigDecimal centerLongitude;

    @Column(name = "radius_km")
    private Double radiusKm;

    @Column(name = "boundaries_json", columnDefinition = "TEXT")
    private String boundariesJson;

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
