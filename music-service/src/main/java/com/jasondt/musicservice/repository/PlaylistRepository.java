package com.jasondt.musicservice.repository;

import com.jasondt.musicservice.model.Artist;
import com.jasondt.musicservice.model.Playlist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {
    Optional<Playlist> findByIdAndDeletedFalse(UUID id);
    List<Playlist> findAllByOwnerIdAndDeletedFalse(UUID ownerId);
    List<Playlist> findTop10ByDeletedFalseAndNameContainingIgnoreCase(String name);

    @Query("""
           select p from Playlist p
           join p.tracks pt
           where p.deleted = false and pt.track.id in :trackIds
           group by p
           order by count(pt) desc
           """)
    List<Playlist> findTopByTrackIds(@Param("trackIds") List<UUID> trackIds, Pageable pageable);

}
