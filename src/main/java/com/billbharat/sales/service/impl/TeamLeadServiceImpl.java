package com.billbharat.sales.service.impl;

import com.billbharat.sales.dto.request.AssignTaskRequest;
import com.billbharat.sales.dto.request.DeliveryAssignRequest;
import com.billbharat.sales.dto.response.UserResponse;
import com.billbharat.sales.entity.*;
import com.billbharat.sales.exception.BadRequestException;
import com.billbharat.sales.exception.ResourceNotFoundException;
import com.billbharat.sales.repository.*;
import com.billbharat.sales.service.TeamLeadService;
import com.billbharat.sales.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TeamLeadServiceImpl implements TeamLeadService {

    private final UserRepository userRepository;
    private final SaleRepository saleRepository;
    private final InventoryRequestRepository inventoryRequestRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final PaperRollDeliveryRepository paperRollDeliveryRepository;
    private final TaskRepository taskRepository;
    private final AttendanceRepository attendanceRepository;
    private final VisitLogRepository visitLogRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMemberLocations(UUID teamLeadId) {
        return userRepository.findAll()
                .stream()
                .filter(u -> teamLeadId.equals(u.getTeamLeadId()))
                .map(user -> {
                    Map<String, Object> location = new HashMap<>();
                    location.put("executiveId", user.getId().toString());
                    location.put("name", user.getName());
                    location.put("phone", user.getPhoneNumber());
                    location.put("status", user.isActive() ? "ACTIVE" : "INACTIVE");
                    location.put("location", null);
                    location.put("lastUpdated", null);
                    location.put("avatarUrl", user.getProfileImageUrl());
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

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAvailableExecutives(UUID teamLeadId, LocalDate date, String status) {
        LocalDate targetDate = date != null ? date : DateUtil.today();

        return userRepository.findAll().stream()
                .filter(u -> teamLeadId.equals(u.getTeamLeadId())
                        && u.getRole() == User.Role.SALES_EXECUTIVE)
                .filter(u -> {
                    if ("ON_LEAVE".equalsIgnoreCase(status)) {
                        return !attendanceRepository.findByUserIdAndAttendanceDate(u.getId(), targetDate).isPresent();
                    }
                    return true;
                })
                .map(user -> {
                    long activeDeliveries = paperRollDeliveryRepository
                            .countByUserIdAndStatus(user.getId(), PaperRollDelivery.Status.PENDING);
                    long todayVisits = visitLogRepository.countByUserIdAndVisitDate(
                            user.getId(), targetDate.atStartOfDay(), targetDate.atTime(23, 59, 59));

                    String currentLoad;
                    if (activeDeliveries >= 5) {
                        currentLoad = "HIGH";
                    } else if (activeDeliveries >= 2) {
                        currentLoad = "MEDIUM";
                    } else {
                        currentLoad = "LOW";
                    }

                    Map<String, Object> exec = new HashMap<>();
                    exec.put("id", user.getId().toString());
                    exec.put("name", user.getName());
                    exec.put("phone", user.getPhoneNumber());
                    exec.put("todayVisits", todayVisits);
                    exec.put("activeDeliveries", activeDeliveries);
                    exec.put("performance", 0);
                    exec.put("territory", null);
                    exec.put("currentLoad", currentLoad);
                    return exec;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPaperRollInventory() {
        return inventoryItemRepository.findAll().stream()
                .filter(InventoryItem::isActive)
                .map(item -> {
                    Map<String, Object> roll = new HashMap<>();
                    roll.put("id", item.getId().toString());
                    roll.put("type", item.getName());
                    roll.put("available", item.getCurrentStock());
                    roll.put("allocated", 0);
                    roll.put("total", item.getCurrentStock());
                    roll.put("sku", item.getSku());
                    return roll;
                })
                .toList();
    }

    @Override
    @Transactional
    public Map<String, Object> assignDelivery(UUID teamLeadId, DeliveryAssignRequest request) {
        UUID executiveId = UUID.fromString(request.getExecutiveId());

        User executive = userRepository.findById(executiveId)
                .orElseThrow(() -> new ResourceNotFoundException("Executive", "id", executiveId));

        if (!teamLeadId.equals(executive.getTeamLeadId())) {
            throw new BadRequestException("Executive does not belong to your team");
        }

        int totalQuantity = 0;
        if (request.getItems() != null) {
            for (Map<String, Object> item : request.getItems()) {
                Object qty = item.get("quantity");
                if (qty instanceof Number) {
                    totalQuantity += ((Number) qty).intValue();
                }
            }
        }

        PaperRollDelivery delivery = PaperRollDelivery.builder()
                .userId(executiveId)
                .merchantId(executiveId)
                .merchantName(executive.getName())
                .quantity(totalQuantity)
                .status(PaperRollDelivery.Status.PENDING)
                .assignedTime(DateUtil.now())
                .notes(request.getNotes())
                .build();

        PaperRollDelivery saved = paperRollDeliveryRepository.save(delivery);

        Map<String, Object> result = new HashMap<>();
        result.put("id", saved.getId().toString());
        result.put("executiveId", executiveId.toString());
        result.put("executiveName", executive.getName());
        result.put("quantity", totalQuantity);
        result.put("status", saved.getStatus().name());
        result.put("assignedTime", saved.getAssignedTime());
        result.put("notes", saved.getNotes());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTeamPerformance(UUID teamLeadId, LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate != null ? startDate : DateUtil.today().withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : DateUtil.today();

        LocalDateTime startDt = start.atStartOfDay();
        LocalDateTime endDt = end.atTime(23, 59, 59);

        List<User> teamMembers = userRepository.findAll().stream()
                .filter(u -> teamLeadId.equals(u.getTeamLeadId()))
                .toList();

        BigDecimal totalSales = BigDecimal.ZERO;
        long totalVisits = 0;
        List<Map<String, Object>> topPerformers = new ArrayList<>();

        for (User member : teamMembers) {
            BigDecimal memberSales = saleRepository.sumFinalAmountByUserIdAndDateRange(
                    member.getId(), startDt, endDt);
            long memberVisits = saleRepository.countByUserIdAndCreatedAtBetween(
                    member.getId(), startDt, endDt);

            if (memberSales != null) totalSales = totalSales.add(memberSales);
            totalVisits += memberVisits;

            Map<String, Object> performer = new HashMap<>();
            performer.put("executiveId", member.getId().toString());
            performer.put("name", member.getName());
            performer.put("sales", memberSales != null ? memberSales : BigDecimal.ZERO);
            performer.put("visits", memberVisits);
            topPerformers.add(performer);
        }

        topPerformers.sort((a, b) -> {
            BigDecimal aSales = (BigDecimal) a.get("sales");
            BigDecimal bSales = (BigDecimal) b.get("sales");
            return bSales.compareTo(aSales);
        });

        for (int i = 0; i < topPerformers.size(); i++) {
            topPerformers.get(i).put("rank", i + 1);
        }

        long avgSalesPerVisit = totalVisits > 0 ? (totalSales.longValue() / totalVisits) : 0;

        Map<String, Object> result = new HashMap<>();
        result.put("totalSales", totalSales);
        result.put("target", BigDecimal.ZERO);
        result.put("achievement", 0);
        result.put("totalVisits", totalVisits);
        result.put("conversionRate", avgSalesPerVisit);
        result.put("topPerformers", topPerformers.stream().limit(5).toList());
        result.put("salesTrend", List.of());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getExecutiveComparison(UUID teamLeadId) {
        LocalDate today = DateUtil.today();
        LocalDateTime monthStart = DateUtil.startOfMonth(today.getYear(), today.getMonthValue());
        LocalDateTime monthEnd = DateUtil.endOfMonth(today.getYear(), today.getMonthValue());

        return userRepository.findAll().stream()
                .filter(u -> teamLeadId.equals(u.getTeamLeadId())
                        && u.getRole() == User.Role.SALES_EXECUTIVE)
                .map(user -> {
                    BigDecimal sales = saleRepository.sumFinalAmountByUserIdAndDateRange(
                            user.getId(), monthStart, monthEnd);
                    long visits = saleRepository.countByUserIdAndCreatedAtBetween(
                            user.getId(), monthStart, monthEnd);
                    BigDecimal totalSalesValue = sales != null ? sales : BigDecimal.ZERO;

                    Map<String, Object> metrics = new HashMap<>();
                    metrics.put("sales", totalSalesValue);
                    metrics.put("visits", visits);
                    metrics.put("conversionRate", 0);
                    BigDecimal avgOrderValue = visits > 0
                            ? totalSalesValue.divide(BigDecimal.valueOf(visits), 2, java.math.RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;
                    metrics.put("avgOrderValue", avgOrderValue);

                    Map<String, Object> exec = new HashMap<>();
                    exec.put("executiveId", user.getId().toString());
                    exec.put("name", user.getName());
                    exec.put("metrics", metrics);
                    return exec;
                })
                .toList();
    }

    @Override
    @Transactional
    public Map<String, Object> assignTask(UUID teamLeadId, AssignTaskRequest request) {
        UUID executiveId = UUID.fromString(request.getExecutiveId());

        User executive = userRepository.findById(executiveId)
                .orElseThrow(() -> new ResourceNotFoundException("Executive", "id", executiveId));

        if (!teamLeadId.equals(executive.getTeamLeadId())) {
            throw new BadRequestException("Executive does not belong to your team");
        }

        Task.Priority priority = Task.Priority.MEDIUM;
        if (request.getPriority() != null) {
            try {
                priority = Task.Priority.valueOf(request.getPriority().toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        LocalDate dueDate = null;
        if (request.getDueDate() != null && !request.getDueDate().isBlank()) {
            try {
                dueDate = LocalDate.parse(request.getDueDate(),
                        java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (java.time.format.DateTimeParseException e) {
                try {
                    dueDate = LocalDate.parse(request.getDueDate().substring(0, 10),
                            java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (java.time.format.DateTimeParseException ignored) {
                }
            }
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .assignedTo(executiveId)
                .assignedBy(teamLeadId)
                .priority(priority)
                .dueDate(dueDate)
                .build();

        Task saved = taskRepository.save(task);

        Map<String, Object> result = new HashMap<>();
        result.put("id", saved.getId().toString());
        result.put("title", saved.getTitle());
        result.put("assignedTo", executiveId.toString());
        result.put("assignedToName", executive.getName());
        result.put("priority", saved.getPriority().name());
        result.put("status", saved.getStatus().name());
        result.put("dueDate", saved.getDueDate());
        result.put("createdAt", saved.getCreatedAt());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTasksOverview(UUID teamLeadId) {
        List<User> teamMembers = userRepository.findAll().stream()
                .filter(u -> teamLeadId.equals(u.getTeamLeadId()))
                .toList();

        long pending = 0, inProgress = 0, completed = 0, overdue = 0;
        List<Map<String, Object>> tasksByExecutive = new ArrayList<>();

        for (User member : teamMembers) {
            long memberPending = taskRepository.countByAssignedToAndStatus(member.getId(), Task.Status.PENDING);
            long memberInProgress = taskRepository.countByAssignedToAndStatus(member.getId(), Task.Status.IN_PROGRESS);
            long memberCompleted = taskRepository.countByAssignedToAndStatus(member.getId(), Task.Status.COMPLETED);

            pending += memberPending;
            inProgress += memberInProgress;
            completed += memberCompleted;

            Map<String, Object> execTasks = new HashMap<>();
            execTasks.put("executiveId", member.getId().toString());
            execTasks.put("name", member.getName());
            execTasks.put("pending", memberPending);
            execTasks.put("inProgress", memberInProgress);
            execTasks.put("completed", memberCompleted);
            tasksByExecutive.add(execTasks);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("pending", pending);
        result.put("inProgress", inProgress);
        result.put("completed", completed);
        result.put("overdue", overdue);
        result.put("tasksByExecutive", tasksByExecutive);
        return result;
    }
}
