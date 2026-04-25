package com.billbharat.sales.repository;

import com.billbharat.sales.entity.UserLessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserLessonProgressRepository extends JpaRepository<UserLessonProgress, UUID> {
    Optional<UserLessonProgress> findByUserIdAndLessonId(UUID userId, UUID lessonId);
    boolean existsByUserIdAndLessonId(UUID userId, UUID lessonId);
    List<UserLessonProgress> findByUserId(UUID userId);
    long countByUserId(UUID userId);

    @Query("SELECT COUNT(ulp) FROM UserLessonProgress ulp " +
           "JOIN TrainingLesson tl ON tl.id = ulp.lessonId " +
           "WHERE ulp.userId = :userId AND tl.moduleId = :moduleId")
    long countCompletedLessonsByUserIdAndModuleId(UUID userId, UUID moduleId);
}
