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
public class TerritoryPerformanceAnalyticsResponse {
    private String territory;
    private String leadName;
    private long sales;
    private BigDecimal revenue;
    private long executiveCount;
    private long visitCount;
}
