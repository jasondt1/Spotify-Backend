package com.jasondt.musicservice.repository;

import com.jasondt.musicservice.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {
    Optional<Playlist> findByIdAndDeletedFalse(UUID id);
    List<Playlist> findAllByOwnerIdAndDeletedFalse(UUID ownerId);
    List<Playlist> findTop10ByDeletedFalseAndNameContainingIgnoreCase(String name);
}
