package com.billbharat.sales.controller;

import com.billbharat.sales.dto.request.TaskRequest;
import com.billbharat.sales.entity.User;
import com.billbharat.sales.exception.UnauthorizedException;
import com.billbharat.sales.repository.UserRepository;
import com.billbharat.sales.service.TaskService;
import com.billbharat.sales.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task management")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByPhoneNumber(auth.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @GetMapping
    @Operation(summary = "Get tasks assigned to current user")
    public ResponseEntity<Map<String, Object>> getTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = taskService.getTasks(getCurrentUser().getId(),
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(ResponseUtil.paginated(result.getContent(), result.getTotalElements(), page + 1, size));
    }

    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<Map<String, Object>> createTask(@Valid @RequestBody TaskRequest request) {
        var response = taskService.createTask(getCurrentUser().getId(), request);
        return ResponseEntity.ok(ResponseUtil.success("Task created", response));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status")
    public ResponseEntity<Map<String, Object>> updateTaskStatus(
            @PathVariable UUID id,
            @RequestParam String status) {
        var response = taskService.updateTaskStatus(id, status, getCurrentUser().getId());
        return ResponseEntity.ok(ResponseUtil.success("Task status updated", response));
    }
}
