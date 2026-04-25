package com.billbharat.sales.controller;

import com.billbharat.sales.dto.request.AnnouncementRequest;
import com.billbharat.sales.entity.TeamAnnouncement;
import com.billbharat.sales.entity.User;
import com.billbharat.sales.exception.UnauthorizedException;
import com.billbharat.sales.repository.TeamAnnouncementRepository;
import com.billbharat.sales.repository.UserRepository;
import com.billbharat.sales.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sales/team")
@RequiredArgsConstructor
@Tag(name = "Team Announcements", description = "Team announcements management")
@SecurityRequirement(name = "bearerAuth")
public class AnnouncementController {

    private final TeamAnnouncementRepository announcementRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByPhoneNumber(auth.getName())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @GetMapping("/announcements")
    @Operation(summary = "Get team announcements")
    public ResponseEntity<Map<String, Object>> getAnnouncements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Map<String, Object>> result = announcementRepository
                .findAllByOrderByIsPinnedDescCreatedAtDesc(
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(a -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", a.getId().toString());
                    map.put("title", a.getTitle());
                    map.put("content", a.getContent());
                    map.put("audience", a.getAudience().name());
                    map.put("isPinned", a.isPinned());
                    map.put("createdBy", a.getCreatedBy().toString());
                    map.put("createdAt", a.getCreatedAt());
                    map.put("expiresAt", a.getExpiresAt());
                    return map;
                });
        return ResponseEntity.ok(ResponseUtil.paginated(result.getContent(), result.getTotalElements(), page + 1, size));
    }

    @PostMapping("/announcements")
    @Operation(summary = "Create a team announcement")
    public ResponseEntity<Map<String, Object>> createAnnouncement(
            @Valid @RequestBody AnnouncementRequest request) {
        User currentUser = getCurrentUser();

        TeamAnnouncement.Audience audience = TeamAnnouncement.Audience.ALL;
        if (StringUtils.hasText(request.getAudience())) {
            try {
                audience = TeamAnnouncement.Audience.valueOf(request.getAudience().toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        TeamAnnouncement announcement = TeamAnnouncement.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .createdBy(currentUser.getId())
                .audience(audience)
                .isPinned(request.isPinned())
                .build();

        TeamAnnouncement saved = announcementRepository.save(announcement);

        Map<String, Object> result = new HashMap<>();
        result.put("id", saved.getId().toString());
        result.put("title", saved.getTitle());
        result.put("content", saved.getContent());
        result.put("audience", saved.getAudience().name());
        result.put("isPinned", saved.isPinned());
        result.put("createdBy", saved.getCreatedBy().toString());
        result.put("createdAt", saved.getCreatedAt());

        return ResponseEntity.ok(ResponseUtil.success("Announcement created", result));
    }
}
