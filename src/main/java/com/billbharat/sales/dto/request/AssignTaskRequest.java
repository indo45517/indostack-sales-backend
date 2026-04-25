package com.billbharat.sales.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignTaskRequest {

    @NotBlank(message = "Executive ID is required")
    private String executiveId;

    private String taskType;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private String dueDate;

    private String priority;

    private String customerId;
}
