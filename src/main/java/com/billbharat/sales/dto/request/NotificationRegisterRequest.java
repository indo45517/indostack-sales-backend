package com.billbharat.sales.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificationRegisterRequest {

    @NotBlank(message = "Device token is required")
    private String deviceToken;

    private String platform;
}
