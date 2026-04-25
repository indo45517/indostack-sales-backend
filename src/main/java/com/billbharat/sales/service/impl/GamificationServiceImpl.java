package com.billbharat.sales.service.impl;

import com.billbharat.sales.dto.request.AwardBadgeRequest;
import com.billbharat.sales.entity.Badge;
import com.billbharat.sales.entity.UserAchievement;
import com.billbharat.sales.exception.BadRequestException;
import com.billbharat.sales.repository.BadgeRepository;
import com.billbharat.sales.repository.UserAchievementRepository;
import com.billbharat.sales.repository.UserRepository;
import com.billbharat.sales.service.GamificationService;
import com.billbharat.sales.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GamificationServiceImpl implements GamificationService {

    private final UserAchievementRepository userAchievementRepository;
    private final BadgeRepository badgeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAchievements(UUID userId) {
        return userAchievementRepository.findByUserIdOrderByUnlockedAtDesc(userId)
                .stream()
                .map(ua -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", ua.getId().toString());
                    map.put("badgeId", ua.getBadgeId().toString());
                    map.put("pointsEarned", ua.getPointsEarned());
                    map.put("unlockedAt", ua.getUnlockedAt());
                    return map;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllBadges() {
        return badgeRepository.findByIsActiveTrueOrderByNameAsc()
                .stream()
                .map(badge -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", badge.getId().toString());
                    map.put("name", badge.getName());
                    map.put("description", badge.getDescription());
                    map.put("iconUrl", badge.getIconUrl());
                    map.put("category", badge.getCategory().name());
                    map.put("level", badge.getLevel().name());
                    map.put("pointsReward", badge.getPointsReward());
                    return map;
                })
                .toList();
    }

    @Override
    @Transactional
    public Map<String, Object> unlockAchievement(UUID userId, String badgeId) {
        UUID badgeUUID = UUID.fromString(badgeId);
        Badge badge = badgeRepository.findById(badgeUUID)
                .orElseThrow(() -> new BadRequestException("Badge not found"));

        if (userAchievementRepository.existsByUserIdAndBadgeId(userId, badgeUUID)) {
            throw new BadRequestException("Achievement already unlocked");
        }

        UserAchievement achievement = UserAchievement.builder()
                .userId(userId)
                .badgeId(badgeUUID)
                .unlockedAt(DateUtil.now())
                .pointsEarned(badge.getPointsReward())
                .build();

        UserAchievement saved = userAchievementRepository.save(achievement);

        Map<String, Object> result = new HashMap<>();
        result.put("id", saved.getId().toString());
        result.put("badge", badge.getName());
        result.put("pointsEarned", saved.getPointsEarned());
        result.put("unlockedAt", saved.getUnlockedAt());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getGamificationStats(UUID userId) {
        int totalPoints = Optional.ofNullable(userAchievementRepository.sumPointsByUserId(userId)).orElse(0);
        long totalBadges = userAchievementRepository.findByUserIdOrderByUnlockedAtDesc(userId).size();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPoints", totalPoints);
        stats.put("totalBadges", totalBadges);
        return stats;
    }

    @Override
    @Transactional
    public Map<String, Object> awardBadge(UUID awardedBy, AwardBadgeRequest request) {
        UUID executiveId = UUID.fromString(request.getExecutiveId());
        UUID badgeUUID = UUID.fromString(request.getBadgeId());

        userRepository.findById(executiveId)
                .orElseThrow(() -> new BadRequestException("Executive not found"));

        Badge badge = badgeRepository.findById(badgeUUID)
                .orElseThrow(() -> new BadRequestException("Badge not found"));

        if (userAchievementRepository.existsByUserIdAndBadgeId(executiveId, badgeUUID)) {
            throw new BadRequestException("Badge already awarded to this executive");
        }

        UserAchievement achievement = UserAchievement.builder()
                .userId(executiveId)
                .badgeId(badgeUUID)
                .unlockedAt(DateUtil.now())
                .pointsEarned(badge.getPointsReward())
                .build();

        UserAchievement saved = userAchievementRepository.save(achievement);

        Map<String, Object> result = new HashMap<>();
        result.put("id", saved.getId().toString());
        result.put("executiveId", executiveId.toString());
        result.put("badge", badge.getName());
        result.put("pointsEarned", saved.getPointsEarned());
        result.put("awardedAt", saved.getUnlockedAt());
        result.put("reason", request.getReason());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTeamLeaderboard(String period, UUID teamId) {
        return userRepository.findAll().stream()
                .filter(u -> teamId == null || teamId.equals(u.getTeamLeadId()))
                .map(user -> {
                    int points = Optional.ofNullable(
                            userAchievementRepository.sumPointsByUserId(user.getId())).orElse(0);
                    long badges = userAchievementRepository
                            .findByUserIdOrderByUnlockedAtDesc(user.getId()).size();

                    Map<String, Object> entry = new HashMap<>();
                    entry.put("userId", user.getId().toString());
                    entry.put("name", user.getName());
                    entry.put("points", points);
                    entry.put("badges", badges);
                    entry.put("avatarUrl", user.getProfileImageUrl());
                    return entry;
                })
                .sorted((a, b) -> Integer.compare(
                        (Integer) b.get("points"), (Integer) a.get("points")))
                .toList();
    }
}
