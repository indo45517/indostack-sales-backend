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
 * User entity representing sales executives, team leads, and owners.
 */
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "phone_number", nullable = false, unique = true, length = 15)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "employee_id", unique = true, length = 20)
    private String employeeId;

    @Column
    private String email;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    @Column(name = "device_token")
    private String deviceToken;

    @Column(name = "team_lead_id", columnDefinition = "uuid")
    private UUID teamLeadId;

    @Column(name = "territory", length = 100)
    private String territory;

    @Column(name = "target", precision = 14, scale = 2)
    private BigDecimal target;

    @Column(name = "commission_rate", precision = 5, scale = 2)
    private BigDecimal commissionRate;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name = "daily_visit_target")
    private Integer dailyVisitTarget;

    @Column(name = "daily_demo_target")
    private Integer dailyDemoTarget;

    @Column(name = "daily_delivery_target")
    private Integer dailyDeliveryTarget;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Role {
        OWNER, SALES_LEAD, SALES_EXECUTIVE
    }
}
