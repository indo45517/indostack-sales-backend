package com.billbharat.sales.dto.response;

import com.billbharat.sales.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private String id;
    private String title;
    private String description;
    private String assignedTo;
    private String assignedBy;
    private String priority;
    private String status;
    private String dueDate;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;

    public static TaskResponse fromEntity(Task task) {
        return TaskResponse.builder()
                .id(task.getId().toString())
                .title(task.getTitle())
                .description(task.getDescription())
                .assignedTo(task.getAssignedTo().toString())
                .assignedBy(task.getAssignedBy().toString())
                .priority(task.getPriority().name())
                .status(task.getStatus().name())
                .dueDate(task.getDueDate() != null ? task.getDueDate().toString() : null)
                .completedAt(task.getCompletedAt())
                .createdAt(task.getCreatedAt())
                .build();
    }
}
