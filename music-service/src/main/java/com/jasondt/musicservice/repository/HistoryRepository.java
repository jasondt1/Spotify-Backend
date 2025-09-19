package com.jasondt.musicservice.repository;

import com.jasondt.musicservice.model.History;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface HistoryRepository extends JpaRepository<History, UUID> {
    long countByTrack_IdAndTrack_DeletedFalse(UUID trackId);
    long countByTrack_IdAndUserIdAndTrack_DeletedFalse(UUID trackId, UUID userId);
    List<History> findByUserIdOrderByPlayedAtDesc(UUID userId, Pageable pageable);
    long countDistinctUserIdByTrack_Artist_IdAndPlayedAtAfter(UUID artistId, Instant after);

    interface TopTrackProjection {
        UUID getTrackId();
        long getPlayCount();
    }

    @Query("""
        select t.id as trackId, coalesce(count(h.id), 0) as playCount
        from Track t
        left join History h on h.track = t
        left join t.otherArtists oa
        where t.deleted = false
        and (t.artist.id = :artistId or oa.id = :artistId)
        group by t.id
        order by playCount desc
    """)
    List<TopTrackProjection> findTopTracksByArtist(@Param("artistId") UUID artistId, Pageable pageable);

}
