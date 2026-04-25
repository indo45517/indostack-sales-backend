package com.billbharat.sales.service;

import com.billbharat.sales.dto.request.BeatPlanRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public interface BeatPlanService {
    Page<Map<String, Object>> getBeatPlans(UUID teamLeadId, LocalDate date, String status, Pageable pageable);
    Map<String, Object> createBeatPlan(UUID teamLeadId, BeatPlanRequest request);
    void deleteBeatPlan(UUID beatPlanId, UUID teamLeadId);
}
