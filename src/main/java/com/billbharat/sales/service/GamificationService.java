package com.billbharat.sales.service;

import com.billbharat.sales.dto.request.AwardBadgeRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface GamificationService {
    List<Map<String, Object>> getAchievements(UUID userId);
    List<Map<String, Object>> getAllBadges();
    Map<String, Object> unlockAchievement(UUID userId, String badgeId);
    Map<String, Object> getGamificationStats(UUID userId);
    Map<String, Object> awardBadge(UUID awardedBy, AwardBadgeRequest request);
    List<Map<String, Object>> getTeamLeaderboard(String period, UUID teamId);
}
