package com.jasondt.musicservice.repository;

import com.jasondt.musicservice.model.PlaylistTrack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrack, UUID> {
    Optional<PlaylistTrack> findByPlaylist_IdAndTrack_IdAndDeletedFalse(UUID playlistId, UUID trackId);
}

