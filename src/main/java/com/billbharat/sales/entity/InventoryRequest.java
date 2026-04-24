package com.billbharat.sales.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Inventory requests from sales executives.
 */
@Entity
@Table(name = "inventory_requests")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "requested_by", nullable = false, columnDefinition = "uuid")
    private UUID requestedBy;

    @Column(name = "inventory_item_id", nullable = false, columnDefinition = "uuid")
    private UUID inventoryItemId;

    @Column(name = "quantity_requested", nullable = false)
    private Integer quantityRequested;

    @Column(name = "quantity_approved")
    private Integer quantityApproved;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.PENDING;

    @Column(name = "approved_by", columnDefinition = "uuid")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Status {
        PENDING, APPROVED, REJECTED, FULFILLED
    }
}
