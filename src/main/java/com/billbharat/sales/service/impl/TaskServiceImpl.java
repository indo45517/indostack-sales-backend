package com.billbharat.sales.service.impl;

import com.billbharat.sales.dto.request.TaskRequest;
import com.billbharat.sales.dto.response.TaskResponse;
import com.billbharat.sales.entity.Task;
import com.billbharat.sales.exception.BadRequestException;
import com.billbharat.sales.exception.ResourceNotFoundException;
import com.billbharat.sales.repository.TaskRepository;
import com.billbharat.sales.service.TaskService;
import com.billbharat.sales.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasks(UUID userId, Pageable pageable) {
        return taskRepository.findByAssignedToOrderByCreatedAtDesc(userId, pageable)
                .map(TaskResponse::fromEntity);
    }

    @Override
    @Transactional
    public TaskResponse createTask(UUID assignedBy, TaskRequest request) {
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .assignedTo(UUID.fromString(request.getAssignedTo()))
                .assignedBy(assignedBy)
                .priority(request.getPriority() != null
                        ? Task.Priority.valueOf(request.getPriority().toUpperCase())
                        : Task.Priority.MEDIUM)
                .dueDate(request.getDueDate() != null ? LocalDate.parse(request.getDueDate()) : null)
                .build();

        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskResponse updateTaskStatus(UUID taskId, String status, UUID userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        if (!task.getAssignedTo().equals(userId)) {
            throw new BadRequestException("Not authorized to update this task");
        }

        Task.Status newStatus;
        try {
            newStatus = Task.Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + status);
        }

        task.setStatus(newStatus);
        if (newStatus == Task.Status.COMPLETED) {
            task.setCompletedAt(DateUtil.now());
        }

        return TaskResponse.fromEntity(taskRepository.save(task));
    }
}
