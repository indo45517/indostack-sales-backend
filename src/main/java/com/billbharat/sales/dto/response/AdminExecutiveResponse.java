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
public class AdminExecutiveResponse {
    private String id;
    private String name;
    private String phone;
    private String email;
    private String employeeId;
    private String assignedLeadId;
    private String assignedLeadName;
    private String territory;
    private Integer dailyVisitTarget;
    private Integer dailyDemoTarget;
    private Integer dailyDeliveryTarget;
    private BigDecimal commissionRate;
    private String joinDate;
    private boolean isActive;
    private long totalSales;
    private BigDecimal totalRevenue;
    private double monthlyAchievementRate;
}
