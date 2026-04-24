package com.billbharat.sales.service.impl;

import com.billbharat.sales.dto.request.VisitRequest;
import com.billbharat.sales.dto.response.VisitResponse;
import com.billbharat.sales.entity.VisitLog;
import com.billbharat.sales.exception.ResourceNotFoundException;
import com.billbharat.sales.repository.VisitLogRepository;
import com.billbharat.sales.service.VisitService;
import com.billbharat.sales.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

    private final VisitLogRepository visitLogRepository;

    @Override
    @Transactional
    public VisitResponse logVisit(UUID userId, VisitRequest request) {
        VisitLog visitLog = VisitLog.builder()
                .userId(userId)
                .shopName(request.getShopName())
                .ownerName(request.getOwnerName())
                .phoneNumber(request.getPhoneNumber())
                .purpose(request.getPurpose())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .photoUrl(request.getPhotoUrl())
                .notes(request.getNotes())
                .visitTime(DateUtil.now())
                .status(StringUtils.hasText(request.getStatus())
                        ? VisitLog.Status.valueOf(request.getStatus().toUpperCase())
                        : VisitLog.Status.VISITED)
                .merchantId(StringUtils.hasText(request.getMerchantId())
                        ? UUID.fromString(request.getMerchantId()) : null)
                .build();

        return VisitResponse.fromEntity(visitLogRepository.save(visitLog));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VisitResponse> getVisits(UUID userId, Pageable pageable) {
        return visitLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(VisitResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public VisitResponse getVisitById(UUID visitId) {
        return visitLogRepository.findById(visitId)
                .map(VisitResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Visit", "id", visitId));
    }
}
