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
public class LeadPerformanceAnalyticsResponse {
    private String leadId;
    private String leadName;
    private String territory;
    private long executiveCount;
    private long teamSales;
    private BigDecimal teamRevenue;
    private BigDecimal teamTarget;
    private double achievementRate;
    private String trend;
}
