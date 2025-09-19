package com.jasondt.musicservice.repository;

import com.jasondt.musicservice.model.QueueItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QueueItemRepository extends JpaRepository<QueueItem, UUID> {
    List<QueueItem> findByUserIdOrderByPositionAsc(UUID userId);
    Optional<QueueItem> findTopByUserIdOrderByPositionDesc(UUID userId);
    Optional<QueueItem> findTopByUserIdOrderByPositionAsc(UUID userId);
    void deleteByUserId(UUID userId);
    long deleteByUserIdAndPositionLessThan(UUID userId, Long position);
}
