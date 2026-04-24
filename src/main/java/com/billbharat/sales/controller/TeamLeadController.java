package com.billbharat.sales.controller;

import com.billbharat.sales.entity.User;
import com.billbharat.sales.exception.UnauthorizedException;
import com.billbharat.sales.repository.UserRepository;
import com.billbharat.sales.service.TeamLeadService;
import com.billbharat.sales.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
@Tag(name = "Team Lead", description = "Team lead management")
@SecurityRequirement(name = "bearerAuth")
public class TeamLeadController {

    private final TeamLeadService teamLeadService;
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
}
