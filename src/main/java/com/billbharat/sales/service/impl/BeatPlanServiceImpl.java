package com.billbharat.sales.service.impl;

import com.billbharat.sales.dto.request.BeatPlanRequest;
import com.billbharat.sales.entity.BeatPlan;
import com.billbharat.sales.entity.Territory;
import com.billbharat.sales.entity.User;
import com.billbharat.sales.exception.BadRequestException;
import com.billbharat.sales.exception.ResourceNotFoundException;
import com.billbharat.sales.repository.BeatPlanRepository;
import com.billbharat.sales.repository.TerritoryRepository;
import com.billbharat.sales.repository.UserRepository;
import com.billbharat.sales.service.BeatPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BeatPlanServiceImpl implements BeatPlanService {

    private final BeatPlanRepository beatPlanRepository;
    private final UserRepository userRepository;
    private final TerritoryRepository territoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Map<String, Object>> getBeatPlans(UUID teamLeadId, LocalDate date, String status, Pageable pageable) {
        Page<BeatPlan> plans;
        if (date != null && status != null) {
            plans = beatPlanRepository.findByTeamLeadIdAndDateAndStatusOrderByCreatedAtDesc(
                    teamLeadId, date, BeatPlan.Status.valueOf(status.toUpperCase()), pageable);
        } else if (date != null) {
            plans = beatPlanRepository.findByTeamLeadIdAndDateOrderByCreatedAtDesc(teamLeadId, date, pageable);
        } else if (status != null) {
            plans = beatPlanRepository.findByTeamLeadIdAndStatusOrderByDateDescCreatedAtDesc(
                    teamLeadId, BeatPlan.Status.valueOf(status.toUpperCase()), pageable);
        } else {
            plans = beatPlanRepository.findByTeamLeadIdOrderByDateDescCreatedAtDesc(teamLeadId, pageable);
        }
        return plans.map(this::toBeatPlanMap);
    }

    @Override
    @Transactional
    public Map<String, Object> createBeatPlan(UUID teamLeadId, BeatPlanRequest request) {
        UUID executiveId = UUID.fromString(request.getExecutiveId());

        User executive = userRepository.findById(executiveId)
                .orElseThrow(() -> new ResourceNotFoundException("Executive", "id", executiveId));

        if (executive.getRole() != User.Role.SALES_EXECUTIVE) {
            throw new BadRequestException("User is not a sales executive");
        }

        if (!teamLeadId.equals(executive.getTeamLeadId())) {
            throw new BadRequestException("Executive does not belong to your team");
        }

        BeatPlan.BeatPlanBuilder builder = BeatPlan.builder()
                .beatName(request.getBeatName())
                .date(LocalDate.parse(request.getDate()))
                .executiveId(executiveId)
                .teamLeadId(teamLeadId)
                .visitTarget(request.getVisitTarget() != null ? request.getVisitTarget() : 0)
                .salesTarget(request.getSalesTarget());

        if (StringUtils.hasText(request.getTerritoryId())) {
            UUID territoryId = UUID.fromString(request.getTerritoryId());
            territoryRepository.findById(territoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Territory", "id", territoryId));
            builder.territoryId(territoryId);
        }

        BeatPlan saved = beatPlanRepository.save(builder.build());
        return toBeatPlanMap(saved);
    }

    @Override
    @Transactional
    public void deleteBeatPlan(UUID beatPlanId, UUID teamLeadId) {
        BeatPlan beatPlan = beatPlanRepository.findById(beatPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("BeatPlan", "id", beatPlanId));

        if (!teamLeadId.equals(beatPlan.getTeamLeadId())) {
            throw new BadRequestException("Beat plan does not belong to your team");
        }

        beatPlanRepository.delete(beatPlan);
    }

    private Map<String, Object> toBeatPlanMap(BeatPlan bp) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", bp.getId().toString());
        map.put("beatName", bp.getBeatName());
        map.put("date", bp.getDate().toString());
        map.put("status", bp.getStatus().name());
        map.put("visitTarget", bp.getVisitTarget());
        map.put("salesTarget", bp.getSalesTarget());
        map.put("createdAt", bp.getCreatedAt());

        if (bp.getTerritoryId() != null) {
            Map<String, Object> territory = new HashMap<>();
            territory.put("id", bp.getTerritoryId().toString());
            territoryRepository.findById(bp.getTerritoryId()).ifPresent(t -> {
                territory.put("name", t.getName());
            });
            map.put("territory", territory);
        } else {
            map.put("territory", null);
        }

        if (bp.getExecutiveId() != null) {
            Map<String, Object> executive = new HashMap<>();
            executive.put("id", bp.getExecutiveId().toString());
            userRepository.findById(bp.getExecutiveId()).ifPresent(u -> {
                executive.put("name", u.getName());
            });
            map.put("executive", executive);
        }

        return map;
    }
}
