package com.billbharat.sales.controller;

import com.billbharat.sales.dto.request.AssignTaskRequest;
import com.billbharat.sales.dto.request.BeatPlanRequest;
import com.billbharat.sales.dto.request.BulkNotificationRequest;
import com.billbharat.sales.dto.request.DeliveryAssignRequest;
import com.billbharat.sales.entity.User;
import com.billbharat.sales.exception.UnauthorizedException;
import com.billbharat.sales.repository.UserRepository;
import com.billbharat.sales.service.BeatPlanService;
import com.billbharat.sales.service.TeamLeadService;
import com.billbharat.sales.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
@Tag(name = "Team Lead", description = "Team lead management")
@SecurityRequirement(name = "bearerAuth")
public class TeamLeadController {

    private final TeamLeadService teamLeadService;
    private final BeatPlanService beatPlanService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByPhoneNumber(auth.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @GetMapping("/teamlead/members/locations")
    @Operation(summary = "Get real-time GPS locations of team members")
    public ResponseEntity<Map<String, Object>> getMemberLocations() {
        var locations = teamLeadService.getMemberLocations(getCurrentUser().getId());
        return ResponseEntity.ok(ResponseUtil.success("Member locations", locations));
    }

    @GetMapping("/team/members")
    @Operation(summary = "Get team members list")
    public ResponseEntity<Map<String, Object>> getTeamMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = teamLeadService.getTeamMembers(getCurrentUser().getId(),
                PageRequest.of(page, size, Sort.by("name").ascending()));
        return ResponseEntity.ok(ResponseUtil.paginated(result.getContent(), result.getTotalElements(), page + 1, size));
    }

    @GetMapping("/team/members/{id}")
    @Operation(summary = "Get team member details")
    public ResponseEntity<Map<String, Object>> getMemberById(@PathVariable UUID id) {
        var member = teamLeadService.getMemberById(id);
        return ResponseEntity.ok(ResponseUtil.success(member));
    }

    @GetMapping("/team/analytics")
    @Operation(summary = "Get team performance analytics")
    public ResponseEntity<Map<String, Object>> getTeamAnalytics() {
        var analytics = teamLeadService.getTeamAnalytics(getCurrentUser().getId());
        return ResponseEntity.ok(ResponseUtil.success("Team analytics", analytics));
    }

    @GetMapping("/approvals")
    @Operation(summary = "Get pending approvals")
    public ResponseEntity<Map<String, Object>> getPendingApprovals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = teamLeadService.getPendingApprovals(getCurrentUser().getId(),
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(ResponseUtil.paginated(result.getContent(), result.getTotalElements(), page + 1, size));
    }

    @PostMapping("/approvals/{id}/approve")
    @Operation(summary = "Approve or reject a request")
    public ResponseEntity<Map<String, Object>> processApproval(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "true") boolean approved) {
        var result = teamLeadService.processApproval(id, approved, getCurrentUser().getId());
        return ResponseEntity.ok(ResponseUtil.success(approved ? "Approved" : "Rejected", result));
    }

    // ── Delivery Assignment System ───────────────────────────────────────────

    @GetMapping("/teamlead/executives/available")
    @Operation(summary = "Get available executives for delivery assignment")
    public ResponseEntity<Map<String, Object>> getAvailableExecutives(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String status) {
        var executives = teamLeadService.getAvailableExecutives(getCurrentUser().getId(), date, status);
        return ResponseEntity.ok(ResponseUtil.success("Available executives", executives));
    }

    @GetMapping("/teamlead/inventory/paper-rolls")
    @Operation(summary = "Get paper roll inventory")
    public ResponseEntity<Map<String, Object>> getPaperRollInventory() {
        var inventory = teamLeadService.getPaperRollInventory();
        Map<String, Object> data = new HashMap<>();
        data.put("paperRolls", inventory);
        data.put("lastUpdated", java.time.LocalDateTime.now());
        return ResponseEntity.ok(ResponseUtil.success("Paper roll inventory", data));
    }

    @PostMapping("/teamlead/deliveries/assign")
    @Operation(summary = "Assign delivery to an executive")
    public ResponseEntity<Map<String, Object>> assignDelivery(@Valid @RequestBody DeliveryAssignRequest request) {
        var result = teamLeadService.assignDelivery(getCurrentUser().getId(), request);
        return ResponseEntity.ok(ResponseUtil.success("Delivery assigned successfully", result));
    }

    // ── Beat Planning System ─────────────────────────────────────────────────

    @GetMapping("/teamlead/beats")
    @Operation(summary = "Get beat plans")
    public ResponseEntity<Map<String, Object>> getBeatPlans(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = beatPlanService.getBeatPlans(getCurrentUser().getId(), date, status,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(ResponseUtil.paginated(result.getContent(), result.getTotalElements(), page + 1, size));
    }

    @PostMapping("/teamlead/beats")
    @Operation(summary = "Create a beat plan")
    public ResponseEntity<Map<String, Object>> createBeatPlan(@Valid @RequestBody BeatPlanRequest request) {
        var result = beatPlanService.createBeatPlan(getCurrentUser().getId(), request);
        return ResponseEntity.ok(ResponseUtil.success("Beat plan created", result));
    }

    @DeleteMapping("/teamlead/beats/{beatId}")
    @Operation(summary = "Delete a beat plan")
    public ResponseEntity<Map<String, Object>> deleteBeatPlan(@PathVariable UUID beatId) {
        beatPlanService.deleteBeatPlan(beatId, getCurrentUser().getId());
        return ResponseEntity.ok(ResponseUtil.success("Beat plan deleted", null));
    }

    // ── Team Analytics ───────────────────────────────────────────────────────

    @GetMapping("/teamlead/analytics/team-performance")
    @Operation(summary = "Get team performance metrics")
    public ResponseEntity<Map<String, Object>> getTeamPerformance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        var result = teamLeadService.getTeamPerformance(getCurrentUser().getId(), startDate, endDate);
        return ResponseEntity.ok(ResponseUtil.success("Team performance", result));
    }

    @GetMapping("/teamlead/analytics/executive-comparison")
    @Operation(summary = "Get executive comparison metrics")
    public ResponseEntity<Map<String, Object>> getExecutiveComparison() {
        var result = teamLeadService.getExecutiveComparison(getCurrentUser().getId());
        return ResponseEntity.ok(ResponseUtil.success("Executive comparison", result));
    }

    // ── Task Management ──────────────────────────────────────────────────────

    @PostMapping("/teamlead/tasks/assign")
    @Operation(summary = "Assign a task to an executive")
    public ResponseEntity<Map<String, Object>> assignTask(@Valid @RequestBody AssignTaskRequest request) {
        var result = teamLeadService.assignTask(getCurrentUser().getId(), request);
        return ResponseEntity.ok(ResponseUtil.success("Task assigned", result));
    }

    @GetMapping("/teamlead/tasks/overview")
    @Operation(summary = "Get team tasks overview")
    public ResponseEntity<Map<String, Object>> getTasksOverview() {
        var result = teamLeadService.getTasksOverview(getCurrentUser().getId());
        return ResponseEntity.ok(ResponseUtil.success("Tasks overview", result));
    }

    // ── Bulk Notifications ───────────────────────────────────────────────────

    @PostMapping("/teamlead/notifications/send")
    @Operation(summary = "Send bulk notifications to team members")
    public ResponseEntity<Map<String, Object>> sendBulkNotifications(
            @Valid @RequestBody BulkNotificationRequest request) {
        // TODO: Integrate with a push notification provider (FCM/APNs) to send actual notifications
        Map<String, Object> result = new HashMap<>();
        result.put("sent", request.getRecipientIds() != null ? request.getRecipientIds().size() : 0);
        result.put("type", request.getType());
        result.put("title", request.getTitle());
        return ResponseEntity.ok(ResponseUtil.success("Notifications sent", result));
    }
}
