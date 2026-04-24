package com.billbharat.sales.controller;

import com.billbharat.sales.entity.User;
import com.billbharat.sales.exception.UnauthorizedException;
import com.billbharat.sales.repository.UserRepository;
import com.billbharat.sales.service.StatsService;
import com.billbharat.sales.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
@Tag(name = "Stats", description = "Performance statistics")
@SecurityRequirement(name = "bearerAuth")
public class StatsController {

    private final StatsService statsService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByPhoneNumber(auth.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @GetMapping("/stats/daily")
    @Operation(summary = "Get daily performance stats")
    public ResponseEntity<Map<String, Object>> getDailyStats() {
        var stats = statsService.getDailyStats(getCurrentUser().getId());
        return ResponseEntity.ok(ResponseUtil.success("Daily stats", stats));
    }

    @GetMapping("/stats/monthly")
    @Operation(summary = "Get monthly performance stats")
    public ResponseEntity<Map<String, Object>> getMonthlyStats(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        LocalDate today = LocalDate.now();
        int y = year != null ? year : today.getYear();
        int m = month != null ? month : today.getMonthValue();
        var stats = statsService.getMonthlyStats(getCurrentUser().getId(), y, m);
        return ResponseEntity.ok(ResponseUtil.success("Monthly stats", stats));
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "Get sales leaderboard")
    public ResponseEntity<Map<String, Object>> getLeaderboard(
            @RequestParam(defaultValue = "10") int limit) {
        var leaderboard = statsService.getLeaderboard(limit);
        return ResponseEntity.ok(ResponseUtil.success("Leaderboard", leaderboard));
    }

    @GetMapping("/earnings")
    @Operation(summary = "Get commission and earnings")
    public ResponseEntity<Map<String, Object>> getEarnings() {
        var earnings = statsService.getEarnings(getCurrentUser().getId());
        return ResponseEntity.ok(ResponseUtil.success("Earnings", earnings));
    }
}
