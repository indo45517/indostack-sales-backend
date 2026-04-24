package com.billbharat.sales.repository;

import com.billbharat.sales.entity.TeamAnnouncement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TeamAnnouncementRepository extends JpaRepository<TeamAnnouncement, UUID> {
    Page<TeamAnnouncement> findByAudienceOrderByIsPinnedDescCreatedAtDesc(TeamAnnouncement.Audience audience, Pageable pageable);
    Page<TeamAnnouncement> findAllByOrderByIsPinnedDescCreatedAtDesc(Pageable pageable);
}
