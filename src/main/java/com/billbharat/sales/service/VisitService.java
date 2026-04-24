package com.billbharat.sales.service;

import com.billbharat.sales.dto.request.VisitRequest;
import com.billbharat.sales.dto.response.VisitResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface VisitService {
    VisitResponse logVisit(UUID userId, VisitRequest request);
    Page<VisitResponse> getVisits(UUID userId, Pageable pageable);
    VisitResponse getVisitById(UUID visitId);
}
