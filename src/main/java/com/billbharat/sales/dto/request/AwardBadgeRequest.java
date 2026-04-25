package com.billbharat.sales.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AwardBadgeRequest {

    @NotBlank(message = "Executive ID is required")
    private String executiveId;

    @NotBlank(message = "Badge ID is required")
    private String badgeId;

    private String reason;

    private String month;
}
