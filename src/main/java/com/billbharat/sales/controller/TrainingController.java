package com.billbharat.sales.controller;

import com.billbharat.sales.entity.User;
import com.billbharat.sales.exception.UnauthorizedException;
import com.billbharat.sales.repository.UserRepository;
import com.billbharat.sales.service.TrainingService;
import com.billbharat.sales.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales/training")
@RequiredArgsConstructor
@Tag(name = "Training", description = "Training center")
@SecurityRequirement(name = "bearerAuth")
public class TrainingController {

    private final TrainingService trainingService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByPhoneNumber(auth.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @GetMapping("/modules")
    @Operation(summary = "Get training modules with lessons")
    public ResponseEntity<Map<String, Object>> getModules() {
        var modules = trainingService.getModules(getCurrentUser().getId());
        return ResponseEntity.ok(ResponseUtil.success("Training modules", modules));
    }

    @PostMapping("/lessons/{lessonId}/complete")
    @Operation(summary = "Mark a lesson as complete")
    public ResponseEntity<Map<String, Object>> markLessonComplete(@PathVariable UUID lessonId) {
        var result = trainingService.markLessonComplete(lessonId, getCurrentUser().getId());
        return ResponseEntity.ok(ResponseUtil.success("Lesson marked as completed", result));
    }

    @GetMapping("/progress")
    @Operation(summary = "Get user training progress")
    public ResponseEntity<Map<String, Object>> getProgress() {
        var progress = trainingService.getProgress(getCurrentUser().getId());
        return ResponseEntity.ok(ResponseUtil.success("Training progress", progress));
    }
}
