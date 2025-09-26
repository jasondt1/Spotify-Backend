package com.jasondt.musicservice.repository;

import com.jasondt.musicservice.model.Album;
import com.jasondt.musicservice.model.Artist;
import com.jasondt.musicservice.model.History;
import com.jasondt.musicservice.model.Track;
import jakarta.persistence.Tuple;
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

    @Query("""
           select t from History h
           join h.track t
           where t.deleted = false
           group by t
           order by count(h) desc
           """)
    List<Track> findTopTracks(Pageable pageable);

    @Query("""
           select ar from History h
           join h.track t
           join t.artist ar
           where t.deleted = false and ar.deleted = false
           group by ar
           order by count(h) desc
           """)
    List<Artist> findTopArtists(Pageable pageable);

    @Query("""
           select al from History h
           join h.track t
           join t.album al
           where t.deleted = false and al.deleted = false
           group by al
           order by count(h) desc
           """)
    List<Album> findTopAlbums(Pageable pageable);

    @Query("""
        SELECT h.track.id AS trackId, COUNT(h) AS playCount
        FROM History h
        WHERE h.userId = :userId
          AND h.playedAt >= :since
          AND h.track.deleted = false
        GROUP BY h.track.id
        ORDER BY COUNT(h) DESC
    """)
    List<Tuple> findTopTracksForUserSince(
            @Param("userId") UUID userId,
            @Param("since") Instant since,
            Pageable pageable
    );

    @Query("""
        SELECT h.track.artist.id AS artistId, COUNT(h) AS playCount
        FROM History h
        WHERE h.userId = :userId
          AND h.playedAt >= :since
          AND h.track.deleted = false
          AND h.track.artist IS NOT NULL
        GROUP BY h.track.artist.id
        ORDER BY COUNT(h) DESC
    """)
    List<Tuple> findTopArtistsForUserSince(
            @Param("userId") UUID userId,
            @Param("since") Instant since,
            Pageable pageable
    );

    @Query("""
        SELECT oa.id AS artistId, COUNT(h) AS playCount
        FROM History h
        JOIN h.track.otherArtists oa
        WHERE h.userId = :userId
          AND h.playedAt >= :since
          AND h.track.deleted = false
        GROUP BY oa.id
        ORDER BY COUNT(h) DESC
    """)
    List<Tuple> findTopOtherArtistsForUserSince(
            @Param("userId") UUID userId,
            @Param("since") Instant since,
            Pageable pageable
    );

    @Query("SELECT h.track.id as trackId, COUNT(h) as playCount " +
            "FROM History h " +
            "WHERE h.track.deleted = false " +
            "GROUP BY h.track.id " +
            "ORDER BY COUNT(h) DESC")
    List<Tuple> findTopTracksAllTime(Pageable pageable);

    @Query("SELECT h.track.artist.id as artistId, COUNT(h) as playCount " +
            "FROM History h " +
            "WHERE h.track.deleted = false AND h.track.artist IS NOT NULL " +
            "GROUP BY h.track.artist.id " +
            "ORDER BY COUNT(h) DESC")
    List<Tuple> findTopArtistsAllTime(Pageable pageable);


}
