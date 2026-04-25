package com.billbharat.sales.repository;

import com.billbharat.sales.entity.TrainingLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TrainingLessonRepository extends JpaRepository<TrainingLesson, UUID> {
    List<TrainingLesson> findByModuleIdAndIsActiveTrueOrderBySortOrderAsc(UUID moduleId);
    long countByModuleIdAndIsActiveTrue(UUID moduleId);
}
