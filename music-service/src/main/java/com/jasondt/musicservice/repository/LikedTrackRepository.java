package com.jasondt.musicservice.repository;

import com.jasondt.musicservice.model.LikedTrack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LikedTrackRepository extends JpaRepository<LikedTrack, UUID> {
    boolean existsByUserIdAndTrack_Id(UUID userId, UUID trackId);
    Optional<LikedTrack> findByUserIdAndTrack_Id(UUID userId, UUID trackId);
    List<LikedTrack> findByUserIdOrderByCreatedAtDesc(UUID userId);
}

