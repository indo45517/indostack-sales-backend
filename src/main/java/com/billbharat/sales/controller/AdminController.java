package com.billbharat.sales.controller;

import com.billbharat.sales.dto.request.*;
import com.billbharat.sales.service.AdminService;
import com.billbharat.sales.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin management endpoints")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('OWNER')")
public class AdminController {

    private final AdminService adminService;

    // ── Leads ────────────────────────────────────────────────────────────────

    @GetMapping("/leads")
    @Operation(summary = "Get all sales leads")
    public ResponseEntity<Map<String, Object>> getLeads() {
        var leads = adminService.getAllLeads();
        return ResponseEntity.ok(ResponseUtil.success("Leads retrieved", leads));
    }

    @GetMapping("/leads/{id}")
    @Operation(summary = "Get lead by ID")
    public ResponseEntity<Map<String, Object>> getLeadById(@PathVariable UUID id) {
        var lead = adminService.getLeadById(id);
        return ResponseEntity.ok(ResponseUtil.success("Lead retrieved", lead));
    }

    @PostMapping("/leads")
    @Operation(summary = "Create a new lead")
    public ResponseEntity<Map<String, Object>> createLead(@Valid @RequestBody CreateLeadRequest request) {
        var lead = adminService.createLead(request);
        return ResponseEntity.ok(ResponseUtil.success("Lead created", lead));
    }

    @PutMapping("/leads/{id}")
    @Operation(summary = "Update a lead")
    public ResponseEntity<Map<String, Object>> updateLead(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateLeadRequest request) {
        var lead = adminService.updateLead(id, request);
        return ResponseEntity.ok(ResponseUtil.success("Lead updated", lead));
    }

    @DeleteMapping("/leads/{id}")
    @Operation(summary = "Delete a lead")
    public ResponseEntity<Map<String, Object>> deleteLead(@PathVariable UUID id) {
        adminService.deleteLead(id);
        return ResponseEntity.ok(ResponseUtil.success("Lead deleted", null));
    }

    // ── Executives ────────────────────────────────────────────────────────────

    @GetMapping("/executives")
    @Operation(summary = "Get all sales executives")
    public ResponseEntity<Map<String, Object>> getExecutives() {
        var executives = adminService.getAllExecutives();
        return ResponseEntity.ok(ResponseUtil.success("Executives retrieved", executives));
    }

    @GetMapping("/executives/{id}")
    @Operation(summary = "Get executive by ID")
    public ResponseEntity<Map<String, Object>> getExecutiveById(@PathVariable UUID id) {
        var executive = adminService.getExecutiveById(id);
        return ResponseEntity.ok(ResponseUtil.success("Executive retrieved", executive));
    }

    @PostMapping("/executives")
    @Operation(summary = "Create a new executive")
    public ResponseEntity<Map<String, Object>> createExecutive(@Valid @RequestBody CreateExecutiveRequest request) {
        var executive = adminService.createExecutive(request);
        return ResponseEntity.ok(ResponseUtil.success("Executive created", executive));
    }

    @PutMapping("/executives/{id}")
    @Operation(summary = "Update an executive")
    public ResponseEntity<Map<String, Object>> updateExecutive(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateExecutiveRequest request) {
        var executive = adminService.updateExecutive(id, request);
        return ResponseEntity.ok(ResponseUtil.success("Executive updated", executive));
    }

    @DeleteMapping("/executives/{id}")
    @Operation(summary = "Delete an executive")
    public ResponseEntity<Map<String, Object>> deleteExecutive(@PathVariable UUID id) {
        adminService.deleteExecutive(id);
        return ResponseEntity.ok(ResponseUtil.success("Executive deleted", null));
    }

    @PutMapping("/executives/{id}/reassign")
    @Operation(summary = "Reassign executive to a different lead")
    public ResponseEntity<Map<String, Object>> reassignExecutive(
            @PathVariable UUID id,
            @Valid @RequestBody ReassignExecutiveRequest request) {
        var executive = adminService.reassignExecutive(id, request);
        return ResponseEntity.ok(ResponseUtil.success("Executive reassigned", executive));
    }

    // ── Analytics ─────────────────────────────────────────────────────────────

    @GetMapping("/analytics/overview")
    @Operation(summary = "Get admin dashboard overview")
    public ResponseEntity<Map<String, Object>> getAnalyticsOverview() {
        var overview = adminService.getOverview();
        return ResponseEntity.ok(ResponseUtil.success("Overview retrieved", overview));
    }

    @GetMapping("/analytics/leads")
    @Operation(summary = "Get lead performance analytics")
    public ResponseEntity<Map<String, Object>> getLeadPerformance() {
        var analytics = adminService.getLeadPerformance();
        return ResponseEntity.ok(ResponseUtil.success("Lead performance retrieved", analytics));
    }

    @GetMapping("/analytics/territories")
    @Operation(summary = "Get territory performance analytics")
    public ResponseEntity<Map<String, Object>> getTerritoryPerformance() {
        var analytics = adminService.getTerritoryPerformance();
        return ResponseEntity.ok(ResponseUtil.success("Territory performance retrieved", analytics));
    }
}
