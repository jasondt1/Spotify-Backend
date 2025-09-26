package com.jasondt.musicservice.repository;

import com.jasondt.musicservice.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArtistRepository extends JpaRepository<Artist, UUID> {
    List<Artist> findAllByDeletedFalseOrderByCreatedAtAsc();
    Optional<Artist> findByIdAndDeletedFalse(UUID id);
    List<Artist> findTop10ByDeletedFalseAndNameContainingIgnoreCase(String name);
    List<Artist> findByIdInAndDeletedFalse(Iterable<UUID> ids);
}
