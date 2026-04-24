package com.billbharat.sales.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface StatsService {
    Map<String, Object> getDailyStats(UUID userId);
    Map<String, Object> getMonthlyStats(UUID userId, int year, int month);
    List<Map<String, Object>> getLeaderboard(int limit);
    Map<String, Object> getEarnings(UUID userId);
}
