package com.billbharat.sales.service;

import com.billbharat.sales.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
}
