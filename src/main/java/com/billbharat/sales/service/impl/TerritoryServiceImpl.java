package com.billbharat.sales.service.impl;

import com.billbharat.sales.dto.request.TerritoryAssignRequest;
import com.billbharat.sales.dto.request.TerritoryBoundariesRequest;
import com.billbharat.sales.dto.request.TerritoryRemoveRequest;
import com.billbharat.sales.dto.request.TerritoryRequest;
import com.billbharat.sales.dto.response.AdminExecutiveResponse;
import com.billbharat.sales.dto.response.TerritoryResponse;
import com.billbharat.sales.entity.Territory;
import com.billbharat.sales.entity.User;
import com.billbharat.sales.exception.ResourceNotFoundException;
import com.billbharat.sales.repository.SaleRepository;
import com.billbharat.sales.repository.TerritoryRepository;
import com.billbharat.sales.repository.UserRepository;
import com.billbharat.sales.service.TerritoryService;
import com.billbharat.sales.util.DateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TerritoryServiceImpl implements TerritoryService {

    private final TerritoryRepository territoryRepository;
    private final UserRepository userRepository;
    private final SaleRepository saleRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<TerritoryResponse> getTerritories(Pageable pageable) {
        return territoryRepository.findByIsActiveTrueOrderByNameAsc(pageable)
                .map(TerritoryResponse::fromEntity);
    }

    @Override
    @Transactional
    public TerritoryResponse createTerritory(TerritoryRequest request) {
        Territory territory = Territory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .assignedTo(StringUtils.hasText(request.getAssignedTo())
                        ? UUID.fromString(request.getAssignedTo()) : null)
                .centerLatitude(request.getCenterLatitude() != null
                        ? BigDecimal.valueOf(request.getCenterLatitude()) : null)
                .centerLongitude(request.getCenterLongitude() != null
                        ? BigDecimal.valueOf(request.getCenterLongitude()) : null)
                .radiusKm(request.getRadiusKm())
                .build();

        return TerritoryResponse.fromEntity(territoryRepository.save(territory));
    }

    @Override
    @Transactional
    public TerritoryResponse updateTerritory(UUID id, TerritoryRequest request) {
        Territory territory = territoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Territory", "id", id));

        territory.setName(request.getName());
        territory.setDescription(request.getDescription());
        if (StringUtils.hasText(request.getAssignedTo())) {
            territory.setAssignedTo(UUID.fromString(request.getAssignedTo()));
        }
        if (request.getCenterLatitude() != null) {
            territory.setCenterLatitude(BigDecimal.valueOf(request.getCenterLatitude()));
        }
        if (request.getCenterLongitude() != null) {
            territory.setCenterLongitude(BigDecimal.valueOf(request.getCenterLongitude()));
        }
        territory.setRadiusKm(request.getRadiusKm());

        return TerritoryResponse.fromEntity(territoryRepository.save(territory));
    }

    @Override
    @Transactional
    public void deleteTerritory(UUID id) {
        Territory territory = territoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Territory", "id", id));
        territory.setActive(false);
        territoryRepository.save(territory);
    }

    @Override
    @Transactional
    public Map<String, Object> updateBoundaries(UUID territoryId, TerritoryBoundariesRequest request) {
        Territory territory = territoryRepository.findById(territoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Territory", "id", territoryId));

        String boundariesJson;
        try {
            boundariesJson = objectMapper.writeValueAsString(request.getBoundaries());
        } catch (Exception e) {
            log.error("Failed to serialize territory boundaries for territory {}: {}", territoryId, e.getMessage());
            boundariesJson = "[]";
        }
        territory.setBoundariesJson(boundariesJson);
        territoryRepository.save(territory);

        Map<String, Object> result = new HashMap<>();
        result.put("id", territory.getId().toString());
        result.put("name", territory.getName());
        result.put("boundaries", request.getBoundaries());
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> assignExecutive(UUID territoryId, TerritoryAssignRequest request) {
        Territory territory = territoryRepository.findById(territoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Territory", "id", territoryId));

        territory.setAssignedTo(UUID.fromString(request.getExecutiveId()));
        territoryRepository.save(territory);

        Map<String, Object> result = new HashMap<>();
        result.put("id", territory.getId().toString());
        result.put("name", territory.getName());
        result.put("assignedTo", request.getExecutiveId());
        result.put("startDate", request.getStartDate());
        result.put("endDate", request.getEndDate());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminExecutiveResponse> getTerritoryExecutives(UUID territoryId) {
        Territory territory = territoryRepository.findById(territoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Territory", "id", territoryId));

        List<User> executives = userRepository.findByTerritoryAndRole(
                territory.getName(), User.Role.SALES_EXECUTIVE);

        Map<UUID, User> leadMap = userRepository.findByRole(User.Role.SALES_LEAD)
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return executives.stream()
                .map(exec -> toExecutiveResponse(exec, leadMap))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeExecutive(UUID territoryId, TerritoryRemoveRequest request) {
        Territory territory = territoryRepository.findById(territoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Territory", "id", territoryId));

        User executive = userRepository.findById(UUID.fromString(request.getExecutiveId()))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getExecutiveId()));

        if (executive.getTerritory() != null && executive.getTerritory().equals(territory.getName())) {
            executive.setTerritory(null);
            userRepository.save(executive);
        }
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
