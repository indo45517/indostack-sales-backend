package com.billbharat.sales.service;

import com.billbharat.sales.dto.request.AssignTaskRequest;
import com.billbharat.sales.dto.request.DeliveryAssignRequest;
import com.billbharat.sales.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TeamLeadService {
    List<Map<String, Object>> getMemberLocations(UUID teamLeadId);
    Page<UserResponse> getTeamMembers(UUID teamLeadId, Pageable pageable);
    UserResponse getMemberById(UUID memberId);
    Map<String, Object> getTeamAnalytics(UUID teamLeadId);
    Page<Map<String, Object>> getPendingApprovals(UUID teamLeadId, Pageable pageable);
    Map<String, Object> processApproval(UUID approvalId, boolean approved, UUID teamLeadId);

    // Delivery Assignment
    List<Map<String, Object>> getAvailableExecutives(UUID teamLeadId, LocalDate date, String status);
    List<Map<String, Object>> getPaperRollInventory();
    Map<String, Object> assignDelivery(UUID teamLeadId, DeliveryAssignRequest request);

    // Team Analytics
    Map<String, Object> getTeamPerformance(UUID teamLeadId, LocalDate startDate, LocalDate endDate);
    List<Map<String, Object>> getExecutiveComparison(UUID teamLeadId);

    // Task Management
    Map<String, Object> assignTask(UUID teamLeadId, AssignTaskRequest request);
    Map<String, Object> getTasksOverview(UUID teamLeadId);
}
