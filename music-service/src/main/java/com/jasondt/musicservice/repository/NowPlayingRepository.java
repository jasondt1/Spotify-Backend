package com.jasondt.musicservice.repository;

import com.jasondt.musicservice.model.NowPlaying;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NowPlayingRepository extends JpaRepository<NowPlaying, UUID> {
    Optional<NowPlaying> findByUserId(UUID userId);
    void deleteByUserId(UUID userId);
}

