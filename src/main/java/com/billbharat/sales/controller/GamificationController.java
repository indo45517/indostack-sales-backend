package com.billbharat.sales.controller;

import com.billbharat.sales.entity.User;
import com.billbharat.sales.exception.UnauthorizedException;
import com.billbharat.sales.repository.UserRepository;
import com.billbharat.sales.service.GamificationService;
import com.billbharat.sales.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
@Tag(name = "Gamification", description = "Badges and achievements")
@SecurityRequirement(name = "bearerAuth")
public class GamificationController {

    private final GamificationService gamificationService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByPhoneNumber(auth.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @GetMapping("/achievements")
    @Operation(summary = "Get user achievements")
    public ResponseEntity<Map<String, Object>> getAchievements() {
        var achievements = gamificationService.getAchievements(getCurrentUser().getId());
        return ResponseEntity.ok(ResponseUtil.success("Achievements", achievements));
    }

    @GetMapping("/badges")
    @Operation(summary = "Get all available badges")
    public ResponseEntity<Map<String, Object>> getBadges() {
        var badges = gamificationService.getAllBadges();
        return ResponseEntity.ok(ResponseUtil.success("Badges", badges));
    }

    @PostMapping("/achievements/unlock")
    @Operation(summary = "Unlock an achievement")
    public ResponseEntity<Map<String, Object>> unlockAchievement(@RequestParam String badgeId) {
        var result = gamificationService.unlockAchievement(getCurrentUser().getId(), badgeId);
        return ResponseEntity.ok(ResponseUtil.success("Achievement unlocked", result));
    }

    @GetMapping("/achievements/stats")
    @Operation(summary = "Get gamification stats")
    public ResponseEntity<Map<String, Object>> getGamificationStats() {
        var stats = gamificationService.getGamificationStats(getCurrentUser().getId());
        return ResponseEntity.ok(ResponseUtil.success("Gamification stats", stats));
    }

    @GetMapping("/leaderboard/badges")
    @Operation(summary = "Get badge leaderboard")
    public ResponseEntity<Map<String, Object>> getBadgeLeaderboard() {
        return ResponseEntity.ok(ResponseUtil.success("Badge leaderboard", java.util.List.of()));
    }
}
