package com.billbharat.sales.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TrainingService {
    List<Map<String, Object>> getModules(UUID userId);
    Map<String, Object> markLessonComplete(UUID lessonId, UUID userId);
    Map<String, Object> getProgress(UUID userId);
}
