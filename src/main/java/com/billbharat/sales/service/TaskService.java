package com.billbharat.sales.service;

import com.billbharat.sales.dto.request.TaskRequest;
import com.billbharat.sales.dto.response.TaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TaskService {
    Page<TaskResponse> getTasks(UUID userId, Pageable pageable);
    TaskResponse createTask(UUID assignedBy, TaskRequest request);
    TaskResponse updateTaskStatus(UUID taskId, String status, UUID userId);
}
