package com.billbharat.sales.service;

import com.billbharat.sales.dto.request.*;
import com.billbharat.sales.dto.response.*;
import com.billbharat.sales.entity.User;
import com.billbharat.sales.exception.BadRequestException;
import com.billbharat.sales.exception.ResourceNotFoundException;
import com.billbharat.sales.repository.AttendanceRepository;
import com.billbharat.sales.repository.SaleRepository;
import com.billbharat.sales.repository.UserRepository;
import com.billbharat.sales.repository.VisitLogRepository;
import com.billbharat.sales.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final SaleRepository saleRepository;
    private final AttendanceRepository attendanceRepository;
    private final VisitLogRepository visitLogRepository;
    private final PasswordEncoder passwordEncoder;

    // ── Leads ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<AdminLeadResponse> getAllLeads() {
        return userRepository.findByRole(User.Role.SALES_LEAD).stream()
                .map(this::toLeadResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminLeadResponse getLeadById(UUID id) {
        User lead = findUserByIdAndRole(id, User.Role.SALES_LEAD);
        return toLeadResponse(lead);
    }

    @Transactional
    public AdminLeadResponse createLead(CreateLeadRequest request) {
        if (userRepository.existsByPhoneNumber(request.getPhone())) {
            throw new BadRequestException("Phone number already registered");
        }

        User lead = User.builder()
                .name(request.getName())
                .phoneNumber(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(User.Role.SALES_LEAD)
                .territory(request.getTerritory())
                .target(request.getTarget())
                .commissionRate(request.getCommissionRate())
                .joinDate(request.getJoinDate() != null ? LocalDate.parse(request.getJoinDate()) : null)
                .build();

        return toLeadResponse(userRepository.save(lead));
    }

    @Transactional
    public AdminLeadResponse updateLead(UUID id, UpdateLeadRequest request) {
        User lead = findUserByIdAndRole(id, User.Role.SALES_LEAD);

        if (request.getName() != null) lead.setName(request.getName());
        if (request.getPhone() != null) {
            if (!request.getPhone().equals(lead.getPhoneNumber()) &&
                    userRepository.existsByPhoneNumber(request.getPhone())) {
                throw new BadRequestException("Phone number already registered");
            }
            lead.setPhoneNumber(request.getPhone());
        }
        if (request.getEmail() != null) lead.setEmail(request.getEmail());
        if (request.getTerritory() != null) lead.setTerritory(request.getTerritory());
        if (request.getTarget() != null) lead.setTarget(request.getTarget());
        if (request.getCommissionRate() != null) lead.setCommissionRate(request.getCommissionRate());
        if (request.getIsActive() != null) lead.setActive(request.getIsActive());

        return toLeadResponse(userRepository.save(lead));
    }

    @Transactional
    public void deleteLead(UUID id) {
        User lead = findUserByIdAndRole(id, User.Role.SALES_LEAD);
        userRepository.delete(lead);
    }

    // ── Executives ───────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<AdminExecutiveResponse> getAllExecutives() {
        List<User> executives = userRepository.findByRole(User.Role.SALES_EXECUTIVE);
        Map<UUID, User> leadMap = buildLeadMap();
        return executives.stream()
                .map(e -> toExecutiveResponse(e, leadMap))
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminExecutiveResponse getExecutiveById(UUID id) {
        User executive = findUserByIdAndRole(id, User.Role.SALES_EXECUTIVE);
        Map<UUID, User> leadMap = buildLeadMap();
        return toExecutiveResponse(executive, leadMap);
    }

    @Transactional
    public AdminExecutiveResponse createExecutive(CreateExecutiveRequest request) {
        if (userRepository.existsByPhoneNumber(request.getPhone())) {
            throw new BadRequestException("Phone number already registered");
        }

        User assignedLead = findUserByIdAndRole(request.getAssignedLeadId(), User.Role.SALES_LEAD);

        User executive = User.builder()
                .name(request.getName())
                .phoneNumber(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(User.Role.SALES_EXECUTIVE)
                .teamLeadId(assignedLead.getId())
                .territory(request.getTerritory())
                .dailyVisitTarget(request.getDailyVisitTarget())
                .dailyDemoTarget(request.getDailyDemoTarget())
                .dailyDeliveryTarget(request.getDailyDeliveryTarget())
                .commissionRate(request.getCommissionRate())
                .joinDate(request.getJoinDate() != null ? LocalDate.parse(request.getJoinDate()) : null)
                .build();

        User saved = userRepository.save(executive);
        Map<UUID, User> leadMap = buildLeadMap();
        return toExecutiveResponse(saved, leadMap);
    }

    @Transactional
    public AdminExecutiveResponse updateExecutive(UUID id, UpdateExecutiveRequest request) {
        User executive = findUserByIdAndRole(id, User.Role.SALES_EXECUTIVE);

        if (request.getName() != null) executive.setName(request.getName());
        if (request.getPhone() != null) {
            if (!request.getPhone().equals(executive.getPhoneNumber()) &&
                    userRepository.existsByPhoneNumber(request.getPhone())) {
                throw new BadRequestException("Phone number already registered");
            }
            executive.setPhoneNumber(request.getPhone());
        }
        if (request.getEmail() != null) executive.setEmail(request.getEmail());
        if (request.getAssignedLeadId() != null) {
            findUserByIdAndRole(request.getAssignedLeadId(), User.Role.SALES_LEAD);
            executive.setTeamLeadId(request.getAssignedLeadId());
        }
        if (request.getTerritory() != null) executive.setTerritory(request.getTerritory());
        if (request.getDailyVisitTarget() != null) executive.setDailyVisitTarget(request.getDailyVisitTarget());
        if (request.getDailyDemoTarget() != null) executive.setDailyDemoTarget(request.getDailyDemoTarget());
        if (request.getDailyDeliveryTarget() != null) executive.setDailyDeliveryTarget(request.getDailyDeliveryTarget());
        if (request.getCommissionRate() != null) executive.setCommissionRate(request.getCommissionRate());
        if (request.getIsActive() != null) executive.setActive(request.getIsActive());

        Map<UUID, User> leadMap = buildLeadMap();
        return toExecutiveResponse(userRepository.save(executive), leadMap);
    }

    @Transactional
    public void deleteExecutive(UUID id) {
        User executive = findUserByIdAndRole(id, User.Role.SALES_EXECUTIVE);
        userRepository.delete(executive);
    }

    @Transactional
    public AdminExecutiveResponse reassignExecutive(UUID id, ReassignExecutiveRequest request) {
        User executive = findUserByIdAndRole(id, User.Role.SALES_EXECUTIVE);
        findUserByIdAndRole(request.getNewLeadId(), User.Role.SALES_LEAD);
        executive.setTeamLeadId(request.getNewLeadId());
        Map<UUID, User> leadMap = buildLeadMap();
        return toExecutiveResponse(userRepository.save(executive), leadMap);
    }

    // ── Analytics ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public AdminOverviewResponse getOverview() {
        LocalDate today = DateUtil.today();
        LocalDateTime monthStart = DateUtil.startOfMonth(today.getYear(), today.getMonthValue());
        LocalDateTime monthEnd = DateUtil.endOfMonth(today.getYear(), today.getMonthValue());

        long totalLeads = userRepository.countByRole(User.Role.SALES_LEAD);
        long activeLeads = userRepository.countByRoleAndIsActive(User.Role.SALES_LEAD, true);
        long totalExecutives = userRepository.countByRole(User.Role.SALES_EXECUTIVE);
        long activeExecutives = userRepository.countByRoleAndIsActive(User.Role.SALES_EXECUTIVE, true);

        long totalSalesAllTime = saleRepository.count();
        BigDecimal totalRevenueAllTime = saleRepository.sumAllFinalAmounts();
        if (totalRevenueAllTime == null) totalRevenueAllTime = BigDecimal.ZERO;

        List<UUID> allExecutiveIds = userRepository.findByRole(User.Role.SALES_EXECUTIVE)
                .stream().map(User::getId).toList();

        long monthlySalesActual = allExecutiveIds.isEmpty() ? 0
                : saleRepository.countByUserIdsAndCreatedAtBetween(allExecutiveIds, monthStart, monthEnd);
        BigDecimal monthlyRevenueActual = allExecutiveIds.isEmpty() ? BigDecimal.ZERO
                : saleRepository.sumFinalAmountByUserIdsAndDateRange(allExecutiveIds, monthStart, monthEnd);
        if (monthlyRevenueActual == null) monthlyRevenueActual = BigDecimal.ZERO;

        BigDecimal monthlySalesTarget = userRepository.findByRole(User.Role.SALES_LEAD).stream()
                .map(l -> l.getTarget() != null ? l.getTarget() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long todayCheckIns = allExecutiveIds.isEmpty() ? 0
                : attendanceRepository.countDistinctUserIdByUserIdInAndAttendanceDate(allExecutiveIds, today);

        return AdminOverviewResponse.builder()
                .totalLeads(totalLeads)
                .activeLeads(activeLeads)
                .totalExecutives(totalExecutives)
                .activeExecutives(activeExecutives)
                .totalSalesAllTime(totalSalesAllTime)
                .totalRevenueAllTime(totalRevenueAllTime)
                .monthlySalesTarget(monthlySalesTarget)
                .monthlySalesActual(monthlySalesActual)
                .monthlyRevenueTarget(BigDecimal.ZERO)
                .monthlyRevenueActual(monthlyRevenueActual)
                .todayCheckIns(todayCheckIns)
                .activeInField(todayCheckIns)
                .pendingApprovals(0)
                .build();
    }

    @Transactional(readOnly = true)
    public List<LeadPerformanceAnalyticsResponse> getLeadPerformance() {
        LocalDate today = DateUtil.today();
        LocalDateTime monthStart = DateUtil.startOfMonth(today.getYear(), today.getMonthValue());
        LocalDateTime monthEnd = DateUtil.endOfMonth(today.getYear(), today.getMonthValue());

        return userRepository.findByRole(User.Role.SALES_LEAD).stream()
                .map(lead -> {
                    List<User> teamMembers = userRepository.findByTeamLeadId(lead.getId());
                    List<UUID> memberIds = teamMembers.stream().map(User::getId).toList();
                    long teamSales = memberIds.isEmpty() ? 0
                            : saleRepository.countByUserIdsAndCreatedAtBetween(memberIds, monthStart, monthEnd);
                    BigDecimal teamRevenue = memberIds.isEmpty() ? BigDecimal.ZERO
                            : saleRepository.sumFinalAmountByUserIdsAndDateRange(memberIds, monthStart, monthEnd);
                    if (teamRevenue == null) teamRevenue = BigDecimal.ZERO;
                    BigDecimal teamTarget = lead.getTarget() != null ? lead.getTarget() : BigDecimal.ZERO;
                    double achievementRate = teamTarget.compareTo(BigDecimal.ZERO) > 0
                            ? teamRevenue.divide(teamTarget, 4, RoundingMode.HALF_UP).doubleValue() * 100
                            : 0.0;

                    return LeadPerformanceAnalyticsResponse.builder()
                            .leadId(lead.getId().toString())
                            .leadName(lead.getName())
                            .territory(lead.getTerritory())
                            .executiveCount(teamMembers.size())
                            .teamSales(teamSales)
                            .teamRevenue(teamRevenue)
                            .teamTarget(teamTarget)
                            .achievementRate(achievementRate)
                            .trend("STABLE")
                            .build();
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TerritoryPerformanceAnalyticsResponse> getTerritoryPerformance() {
        LocalDate today = DateUtil.today();
        LocalDateTime monthStart = DateUtil.startOfMonth(today.getYear(), today.getMonthValue());
        LocalDateTime monthEnd = DateUtil.endOfMonth(today.getYear(), today.getMonthValue());

        return userRepository.findByRole(User.Role.SALES_LEAD).stream()
                .filter(lead -> lead.getTerritory() != null)
                .map(lead -> {
                    List<User> teamMembers = userRepository.findByTeamLeadId(lead.getId());
                    List<UUID> memberIds = teamMembers.stream().map(User::getId).toList();
                    long sales = memberIds.isEmpty() ? 0
                            : saleRepository.countByUserIdsAndCreatedAtBetween(memberIds, monthStart, monthEnd);
                    BigDecimal revenue = memberIds.isEmpty() ? BigDecimal.ZERO
                            : saleRepository.sumFinalAmountByUserIdsAndDateRange(memberIds, monthStart, monthEnd);
                    if (revenue == null) revenue = BigDecimal.ZERO;
                    long visitCount = memberIds.isEmpty() ? 0
                            : visitLogRepository.countByUserIdsAndCreatedAtBetween(memberIds, monthStart, monthEnd);

                    return TerritoryPerformanceAnalyticsResponse.builder()
                            .territory(lead.getTerritory())
                            .leadName(lead.getName())
                            .sales(sales)
                            .revenue(revenue)
                            .executiveCount(teamMembers.size())
                            .visitCount(visitCount)
                            .build();
                })
                .toList();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private User findUserByIdAndRole(UUID id, User.Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        if (user.getRole() != role) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        return user;
    }

    private Map<UUID, User> buildLeadMap() {
        return userRepository.findByRole(User.Role.SALES_LEAD).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    private AdminLeadResponse toLeadResponse(User lead) {
        LocalDate today = DateUtil.today();
        LocalDateTime monthStart = DateUtil.startOfMonth(today.getYear(), today.getMonthValue());
        LocalDateTime monthEnd = DateUtil.endOfMonth(today.getYear(), today.getMonthValue());

        List<User> teamMembers = userRepository.findByTeamLeadId(lead.getId());
        List<UUID> memberIds = teamMembers.stream().map(User::getId).toList();
        long totalSales = memberIds.isEmpty() ? 0
                : saleRepository.countByUserIdsAndCreatedAtBetween(memberIds, monthStart, monthEnd);
        BigDecimal totalRevenue = memberIds.isEmpty() ? BigDecimal.ZERO
                : saleRepository.sumFinalAmountByUserIdsAndDateRange(memberIds, monthStart, monthEnd);
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        BigDecimal target = lead.getTarget() != null ? lead.getTarget() : BigDecimal.ZERO;
        double achievementRate = target.compareTo(BigDecimal.ZERO) > 0
                ? totalRevenue.divide(target, 4, RoundingMode.HALF_UP).doubleValue() * 100
                : 0.0;

        return AdminLeadResponse.builder()
                .id(lead.getId().toString())
                .name(lead.getName())
                .phone(lead.getPhoneNumber())
                .email(lead.getEmail())
                .employeeId(lead.getEmployeeId())
                .territory(lead.getTerritory())
                .target(lead.getTarget())
                .commissionRate(lead.getCommissionRate())
                .joinDate(lead.getJoinDate() != null ? lead.getJoinDate().toString() : null)
                .isActive(lead.isActive())
                .executiveCount(teamMembers.size())
                .totalSales(totalSales)
                .totalRevenue(totalRevenue)
                .teamAchievementRate(achievementRate)
                .performanceTrend("STABLE")
                .build();
    }

    private AdminExecutiveResponse toExecutiveResponse(User executive, Map<UUID, User> leadMap) {
        LocalDate today = DateUtil.today();
        LocalDateTime monthStart = DateUtil.startOfMonth(today.getYear(), today.getMonthValue());
        LocalDateTime monthEnd = DateUtil.endOfMonth(today.getYear(), today.getMonthValue());

        long totalSales = saleRepository.countByUserIdAndCreatedAtBetween(executive.getId(), monthStart, monthEnd);
        BigDecimal totalRevenue = saleRepository.sumFinalAmountByUserIdAndDateRange(executive.getId(), monthStart, monthEnd);
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        User assignedLead = executive.getTeamLeadId() != null ? leadMap.get(executive.getTeamLeadId()) : null;
        BigDecimal target = assignedLead != null && assignedLead.getTarget() != null
                ? assignedLead.getTarget()
                : BigDecimal.ZERO;
        double achievementRate = target.compareTo(BigDecimal.ZERO) > 0
                ? totalRevenue.divide(target, 4, RoundingMode.HALF_UP).doubleValue() * 100
                : 0.0;

        return AdminExecutiveResponse.builder()
                .id(executive.getId().toString())
                .name(executive.getName())
                .phone(executive.getPhoneNumber())
                .email(executive.getEmail())
                .employeeId(executive.getEmployeeId())
                .assignedLeadId(executive.getTeamLeadId() != null ? executive.getTeamLeadId().toString() : null)
                .assignedLeadName(assignedLead != null ? assignedLead.getName() : null)
                .territory(executive.getTerritory())
                .dailyVisitTarget(executive.getDailyVisitTarget())
                .dailyDemoTarget(executive.getDailyDemoTarget())
                .dailyDeliveryTarget(executive.getDailyDeliveryTarget())
                .commissionRate(executive.getCommissionRate())
                .joinDate(executive.getJoinDate() != null ? executive.getJoinDate().toString() : null)
                .isActive(executive.isActive())
                .totalSales(totalSales)
                .totalRevenue(totalRevenue)
                .monthlyAchievementRate(achievementRate)
                .build();
    }
}
