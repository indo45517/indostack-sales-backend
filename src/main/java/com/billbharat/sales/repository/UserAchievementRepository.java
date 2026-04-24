package com.billbharat.sales.repository;

import com.billbharat.sales.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, UUID> {
    List<UserAchievement> findByUserIdOrderByUnlockedAtDesc(UUID userId);
    boolean existsByUserIdAndBadgeId(UUID userId, UUID badgeId);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(ua.pointsEarned), 0) FROM UserAchievement ua WHERE ua.userId = :userId")
    Integer sumPointsByUserId(@org.springframework.data.repository.query.Param("userId") UUID userId);
}
