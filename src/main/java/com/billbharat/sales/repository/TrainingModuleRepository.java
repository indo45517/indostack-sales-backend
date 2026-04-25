package com.billbharat.sales.repository;

import com.billbharat.sales.entity.TrainingModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TrainingModuleRepository extends JpaRepository<TrainingModule, UUID> {
    List<TrainingModule> findByIsActiveTrueOrderBySortOrderAsc();
}
