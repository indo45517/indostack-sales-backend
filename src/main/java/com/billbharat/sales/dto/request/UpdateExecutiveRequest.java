package com.billbharat.sales.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UpdateExecutiveRequest {

    private String name;

    private String phone;

    private String email;

    private UUID assignedLeadId;

    private String territory;

    private Integer dailyVisitTarget;

    private Integer dailyDemoTarget;

    private Integer dailyDeliveryTarget;

    private BigDecimal commissionRate;

    private Boolean isActive;
}
