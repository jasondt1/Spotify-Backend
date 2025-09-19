package com.jasondt.musicservice.repository;

import com.jasondt.musicservice.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, UUID> {
    List<Album> findAllByDeletedFalse();
    Optional<Album> findByIdAndDeletedFalse(UUID id);
    List<Album> findTop10ByDeletedFalseAndTitleContainingIgnoreCase(String title);

    List<Album> findTop10ByDeletedFalseAndArtistIdIn(List<UUID> artistIds);
}
