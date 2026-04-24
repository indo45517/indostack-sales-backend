package com.billbharat.sales.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Tracks user achievement unlocks.
 */
@Entity
@Table(name = "user_achievements")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "badge_id", nullable = false, columnDefinition = "uuid")
    private UUID badgeId;

    @Column(name = "unlocked_at")
    private LocalDateTime unlockedAt;

    @Column(name = "points_earned")
    @Builder.Default
    private Integer pointsEarned = 0;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
