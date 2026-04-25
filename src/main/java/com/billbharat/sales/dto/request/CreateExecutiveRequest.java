package com.billbharat.sales.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateExecutiveRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Invalid phone number format")
    private String phone;

    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Assigned lead ID is required")
    private UUID assignedLeadId;

    private String territory;

    private Integer dailyVisitTarget;

    private Integer dailyDemoTarget;

    private Integer dailyDeliveryTarget;

    private BigDecimal commissionRate;

    @NotBlank(message = "Join date is required")
    private String joinDate;
}
