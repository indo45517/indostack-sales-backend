package com.billbharat.sales.service.impl;

import com.billbharat.sales.dto.response.UserResponse;
import com.billbharat.sales.entity.InventoryRequest;
import com.billbharat.sales.exception.ResourceNotFoundException;
import com.billbharat.sales.repository.InventoryRequestRepository;
import com.billbharat.sales.repository.SaleRepository;
import com.billbharat.sales.repository.UserRepository;
import com.billbharat.sales.service.TeamLeadService;
import com.billbharat.sales.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TeamLeadServiceImpl implements TeamLeadService {

    private final UserRepository userRepository;
    private final SaleRepository saleRepository;
    private final InventoryRequestRepository inventoryRequestRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMemberLocations(UUID teamLeadId) {
        return userRepository.findAll()
                .stream()
                .filter(u -> teamLeadId.equals(u.getTeamLeadId()))
                .map(user -> {
                    Map<String, Object> location = new HashMap<>();
                    location.put("userId", user.getId().toString());
                    location.put("name", user.getName());
                    location.put("latitude", null);
                    location.put("longitude", null);
                    location.put("lastUpdated", null);
                    return location;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getTeamMembers(UUID teamLeadId, Pageable pageable) {
        List<UserResponse> members = userRepository.findAll()
                .stream()
                .filter(u -> teamLeadId.equals(u.getTeamLeadId()))
                .map(UserResponse::fromEntity)
                .toList();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), members.size());
        return new PageImpl<>(members.subList(start, end), pageable, members.size());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getMemberById(UUID memberId) {
        return userRepository.findById(memberId)
                .map(UserResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", memberId));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTeamAnalytics(UUID teamLeadId) {
        LocalDate today = DateUtil.today();
        LocalDateTime monthStart = DateUtil.startOfMonth(today.getYear(), today.getMonthValue());
        LocalDateTime monthEnd = DateUtil.endOfMonth(today.getYear(), today.getMonthValue());

        long totalMembers = userRepository.findAll().stream()
                .filter(u -> teamLeadId.equals(u.getTeamLeadId())).count();

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalMembers", totalMembers);
        analytics.put("month", today.getMonthValue());
        analytics.put("year", today.getYear());
        return analytics;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Map<String, Object>> getPendingApprovals(UUID teamLeadId, Pageable pageable) {
        return inventoryRequestRepository.findByStatusOrderByCreatedAtDesc(InventoryRequest.Status.PENDING, pageable)
                .map(req -> {
                    Map<String, Object> approval = new HashMap<>();
                    approval.put("id", req.getId().toString());
                    approval.put("requestedBy", req.getRequestedBy().toString());
                    approval.put("quantityRequested", req.getQuantityRequested());
                    approval.put("status", req.getStatus().name());
                    approval.put("notes", req.getNotes());
                    approval.put("createdAt", req.getCreatedAt());
                    return approval;
                });
    }

    @Override
    @Transactional
    public Map<String, Object> processApproval(UUID approvalId, boolean approved, UUID teamLeadId) {
        InventoryRequest request = inventoryRequestRepository.findById(approvalId)
                .orElseThrow(() -> new ResourceNotFoundException("Approval", "id", approvalId));

        request.setStatus(approved ? InventoryRequest.Status.APPROVED : InventoryRequest.Status.REJECTED);
        request.setApprovedBy(teamLeadId);
        request.setApprovedAt(DateUtil.now());
        if (approved) {
            request.setQuantityApproved(request.getQuantityRequested());
        }

        inventoryRequestRepository.save(request);

        Map<String, Object> result = new HashMap<>();
        result.put("id", approvalId.toString());
        result.put("status", request.getStatus().name());
        result.put("approved", approved);
        return result;
    }
}
