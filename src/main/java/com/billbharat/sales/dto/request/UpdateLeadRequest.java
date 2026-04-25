package com.billbharat.sales.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateLeadRequest {

    private String name;

    private String phone;

    private String email;

    private String territory;

    private BigDecimal target;

    private BigDecimal commissionRate;

    private Boolean isActive;
}
