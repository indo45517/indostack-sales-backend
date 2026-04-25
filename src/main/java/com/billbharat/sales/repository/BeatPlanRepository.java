package com.billbharat.sales.repository;

import com.billbharat.sales.entity.BeatPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface BeatPlanRepository extends JpaRepository<BeatPlan, UUID> {
    Page<BeatPlan> findByTeamLeadIdOrderByDateDescCreatedAtDesc(UUID teamLeadId, Pageable pageable);
    Page<BeatPlan> findByTeamLeadIdAndDateOrderByCreatedAtDesc(UUID teamLeadId, LocalDate date, Pageable pageable);
    Page<BeatPlan> findByTeamLeadIdAndStatusOrderByDateDescCreatedAtDesc(UUID teamLeadId, BeatPlan.Status status, Pageable pageable);
    Page<BeatPlan> findByTeamLeadIdAndDateAndStatusOrderByCreatedAtDesc(UUID teamLeadId, LocalDate date, BeatPlan.Status status, Pageable pageable);
    List<BeatPlan> findByExecutiveIdAndDate(UUID executiveId, LocalDate date);
}
