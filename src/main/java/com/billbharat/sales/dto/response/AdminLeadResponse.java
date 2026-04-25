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
public class AdminLeadResponse {
    private String id;
    private String name;
    private String phone;
    private String email;
    private String employeeId;
    private String territory;
    private BigDecimal target;
    private BigDecimal commissionRate;
    private String joinDate;
    private boolean isActive;
    private long executiveCount;
    private long totalSales;
    private BigDecimal totalRevenue;
    private double teamAchievementRate;
    private String performanceTrend;
}
