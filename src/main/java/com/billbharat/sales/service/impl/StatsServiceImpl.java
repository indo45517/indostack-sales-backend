package com.billbharat.sales.service.impl;

import com.billbharat.sales.entity.User;
import com.billbharat.sales.repository.*;
import com.billbharat.sales.service.StatsService;
import com.billbharat.sales.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final SaleRepository saleRepository;
    private final VisitLogRepository visitLogRepository;
    private final AttendanceRepository attendanceRepository;
    private final CommissionRepository commissionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDailyStats(UUID userId) {
        LocalDate today = DateUtil.today();
        LocalDateTime start = DateUtil.startOfDay(today);
        LocalDateTime end = DateUtil.endOfDay(today);

        long totalSalesCount = saleRepository.countByUserIdAndCreatedAtBetween(userId, start, end);
        BigDecimal totalSalesAmount = saleRepository.sumFinalAmountByUserIdAndDateRange(userId, start, end);
        long totalVisits = visitLogRepository.countByUserIdAndCreatedAtBetween(userId, start, end);

        Map<String, Object> stats = new HashMap<>();
        stats.put("date", today.toString());
        stats.put("totalSalesCount", totalSalesCount);
        stats.put("totalSalesAmount", totalSalesAmount != null ? totalSalesAmount : BigDecimal.ZERO);
        stats.put("totalVisits", totalVisits);
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getMonthlyStats(UUID userId, int year, int month) {
        LocalDateTime start = DateUtil.startOfMonth(year, month);
        LocalDateTime end = DateUtil.endOfMonth(year, month);

        long totalSalesCount = saleRepository.countByUserIdAndCreatedAtBetween(userId, start, end);
        BigDecimal totalSalesAmount = saleRepository.sumFinalAmountByUserIdAndDateRange(userId, start, end);
        long totalVisits = visitLogRepository.countByUserIdAndCreatedAtBetween(userId, start, end);
        BigDecimal totalCommission = commissionRepository.sumCommissionByUserIdAndDateRange(userId, start, end);

        Map<String, Object> stats = new HashMap<>();
        stats.put("year", year);
        stats.put("month", month);
        stats.put("totalSalesCount", totalSalesCount);
        stats.put("totalSalesAmount", totalSalesAmount != null ? totalSalesAmount : BigDecimal.ZERO);
        stats.put("totalVisits", totalVisits);
        stats.put("totalCommission", totalCommission != null ? totalCommission : BigDecimal.ZERO);
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getLeaderboard(int limit) {
        LocalDate today = DateUtil.today();
        LocalDateTime start = DateUtil.startOfMonth(today.getYear(), today.getMonthValue());
        LocalDateTime end = DateUtil.endOfMonth(today.getYear(), today.getMonthValue());

        List<Object[]> results = saleRepository.findLeaderboardByDateRange(start, end, PageRequest.of(0, limit));
        List<Map<String, Object>> leaderboard = new ArrayList<>();

        int rank = 1;
        for (Object[] row : results) {
            UUID uid = (UUID) row[0];
            BigDecimal totalSales = (BigDecimal) row[1];

            Optional<User> user = userRepository.findById(uid);
            Map<String, Object> entry = new HashMap<>();
            entry.put("rank", rank++);
            entry.put("userId", uid.toString());
            entry.put("name", user.map(User::getName).orElse("Unknown"));
            entry.put("totalSales", totalSales);
            leaderboard.add(entry);
        }
        return leaderboard;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getEarnings(UUID userId) {
        LocalDate today = DateUtil.today();
        LocalDateTime monthStart = DateUtil.startOfMonth(today.getYear(), today.getMonthValue());
        LocalDateTime monthEnd = DateUtil.endOfMonth(today.getYear(), today.getMonthValue());

        BigDecimal monthlyCommission = commissionRepository.sumCommissionByUserIdAndDateRange(userId, monthStart, monthEnd);

        Map<String, Object> earnings = new HashMap<>();
        earnings.put("monthlyCommission", monthlyCommission != null ? monthlyCommission : BigDecimal.ZERO);
        earnings.put("year", today.getYear());
        earnings.put("month", today.getMonthValue());
        return earnings;
    }
}
