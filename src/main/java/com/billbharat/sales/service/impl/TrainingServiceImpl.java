package com.billbharat.sales.service.impl;

import com.billbharat.sales.entity.TrainingLesson;
import com.billbharat.sales.entity.TrainingModule;
import com.billbharat.sales.entity.UserLessonProgress;
import com.billbharat.sales.exception.ResourceNotFoundException;
import com.billbharat.sales.repository.TrainingLessonRepository;
import com.billbharat.sales.repository.TrainingModuleRepository;
import com.billbharat.sales.repository.UserLessonProgressRepository;
import com.billbharat.sales.service.TrainingService;
import com.billbharat.sales.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingModuleRepository trainingModuleRepository;
    private final TrainingLessonRepository trainingLessonRepository;
    private final UserLessonProgressRepository userLessonProgressRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getModules(UUID userId) {
        List<TrainingModule> modules = trainingModuleRepository.findByIsActiveTrueOrderBySortOrderAsc();
        Set<UUID> completedLessonIds = new HashSet<>();
        userLessonProgressRepository.findByUserId(userId)
                .forEach(p -> completedLessonIds.add(p.getLessonId()));

        return modules.stream().map(module -> {
            List<TrainingLesson> lessons =
                    trainingLessonRepository.findByModuleIdAndIsActiveTrueOrderBySortOrderAsc(module.getId());

            long completedCount = lessons.stream()
                    .filter(l -> completedLessonIds.contains(l.getId()))
                    .count();

            List<Map<String, Object>> lessonMaps = lessons.stream().map(lesson -> {
                Map<String, Object> lm = new HashMap<>();
                lm.put("id", lesson.getId().toString());
                lm.put("title", lesson.getTitle());
                lm.put("duration", lesson.getDuration());
                lm.put("completed", completedLessonIds.contains(lesson.getId()));
                lm.put("content", lesson.getContent());
                return lm;
            }).toList();

            Map<String, Object> mm = new HashMap<>();
            mm.put("id", module.getId().toString());
            mm.put("title", module.getTitle());
            mm.put("icon", module.getIconEmoji());
            mm.put("lessonsCount", lessons.size());
            mm.put("completedLessons", completedCount);
            mm.put("estimatedTime", module.getEstimatedTime());
            mm.put("lessons", lessonMaps);
            return mm;
        }).toList();
    }

    @Override
    @Transactional
    public Map<String, Object> markLessonComplete(UUID lessonId, UUID userId) {
        TrainingLesson lesson = trainingLessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", lessonId));

        if (!userLessonProgressRepository.existsByUserIdAndLessonId(userId, lessonId)) {
            UserLessonProgress progress = UserLessonProgress.builder()
                    .userId(userId)
                    .lessonId(lessonId)
                    .completedAt(DateUtil.now())
                    .build();
            userLessonProgressRepository.save(progress);
        }

        long totalLessons = trainingLessonRepository.count();
        long completedLessons = userLessonProgressRepository.countByUserId(userId);
        int percentage = totalLessons > 0 ? (int) ((completedLessons * 100) / totalLessons) : 0;

        Map<String, Object> progressMap = new HashMap<>();
        progressMap.put("totalLessons", totalLessons);
        progressMap.put("completedLessons", completedLessons);
        progressMap.put("percentage", percentage);

        Map<String, Object> result = new HashMap<>();
        result.put("lessonId", lessonId.toString());
        result.put("completedAt", DateUtil.now());
        result.put("progress", progressMap);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getProgress(UUID userId) {
        List<TrainingModule> modules = trainingModuleRepository.findByIsActiveTrueOrderBySortOrderAsc();
        long totalLessons = trainingLessonRepository.count();
        long completedLessons = userLessonProgressRepository.countByUserId(userId);

        long completedModules = modules.stream().filter(module -> {
            long moduleLessons = trainingLessonRepository.countByModuleIdAndIsActiveTrue(module.getId());
            long completedForModule = userLessonProgressRepository
                    .countCompletedLessonsByUserIdAndModuleId(userId, module.getId());
            return moduleLessons > 0 && completedForModule == moduleLessons;
        }).count();

        int percentage = totalLessons > 0 ? (int) ((completedLessons * 100) / totalLessons) : 0;

        Map<String, Object> result = new HashMap<>();
        result.put("totalModules", modules.size());
        result.put("completedModules", completedModules);
        result.put("totalLessons", totalLessons);
        result.put("completedLessons", completedLessons);
        result.put("percentage", percentage);
        return result;
    }
}
