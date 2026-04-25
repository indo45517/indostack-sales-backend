package com.billbharat.sales.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOverviewResponse {
    private long totalLeads;
    private long activeLeads;
    private long totalExecutives;
    private long activeExecutives;
    private long totalSalesAllTime;
    private BigDecimal totalRevenueAllTime;
    private BigDecimal monthlySalesTarget;
    private long monthlySalesActual;
    private BigDecimal monthlyRevenueTarget;
    private BigDecimal monthlyRevenueActual;
    private long todayCheckIns;
    private long activeInField;
    private long pendingApprovals;
}
