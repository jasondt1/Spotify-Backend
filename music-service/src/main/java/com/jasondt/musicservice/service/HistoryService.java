package com.jasondt.musicservice.service;

import com.jasondt.musicservice.dto.*;
import com.jasondt.musicservice.exception.NotFoundException;
import com.jasondt.musicservice.mapper.AlbumMapper;
import com.jasondt.musicservice.model.Artist;
import com.jasondt.musicservice.model.History;
import com.jasondt.musicservice.model.Track;
import com.jasondt.musicservice.repository.ArtistRepository;
import com.jasondt.musicservice.repository.HistoryRepository;
import com.jasondt.musicservice.repository.TrackRepository;
import jakarta.persistence.Tuple;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepo;
    private final TrackRepository trackRepo;
    private final AlbumMapper albumMapper;
    private final ArtistRepository artistRepo;
    private static final int WINDOW_DAYS = 30;
    private static final int LIMIT = 20;

    @Transactional
    public void recordPlay(UUID userId, UUID trackId) {
        Track track = trackRepo.findByIdAndDeletedFalse(trackId)
                .orElseThrow(() -> new NotFoundException("Track not found with ID: " + trackId));
        History h = new History();
        h.setUserId(userId);
        h.setTrack(track);
        historyRepo.save(h);
        long total = historyRepo.countByTrack_IdAndTrack_DeletedFalse(trackId);
        long mine = historyRepo.countByTrack_IdAndUserIdAndTrack_DeletedFalse(trackId, userId);
    }

    public PlayCountResponseDto getTrackPlayCount(UUID trackId) {
        trackRepo.findByIdAndDeletedFalse(trackId)
                .orElseThrow(() -> new NotFoundException("Track not found with ID: " + trackId));
        long total = historyRepo.countByTrack_IdAndTrack_DeletedFalse(trackId);
        return new PlayCountResponseDto(trackId, total, null);
    }
    public List<HistoryResponseDto> getUserHistory(UUID userId, int page, int size) {
        var items = historyRepo.findByUserIdOrderByPlayedAtDesc(userId, PageRequest.of(page, size));

        var dtos = items.stream().map(ev -> {
            var track = ev.getTrack();

            HistoryResponseDto dto = new HistoryResponseDto();
            dto.setTrackId(track.getId());
            dto.setTitle(track.getTitle());
            dto.setDuration(track.getDuration());
            dto.setAudio(track.getAudio());
            dto.setCreatedAt(track.getCreatedAt());
            dto.setUpdatedAt(track.getUpdatedAt());

            List<ArtistSimpleDto> artistDtos = new ArrayList<>();
            if (track.getArtist() != null) {
                ArtistSimpleDto mainArtist = new ArtistSimpleDto();
                mainArtist.setId(track.getArtist().getId());
                mainArtist.setName(track.getArtist().getName());
                artistDtos.add(mainArtist);
            }
            if (track.getOtherArtists() != null && !track.getOtherArtists().isEmpty()) {
                artistDtos.addAll(
                        track.getOtherArtists().stream().map(artist -> {
                            ArtistSimpleDto a = new ArtistSimpleDto();
                            a.setId(artist.getId());
                            a.setName(artist.getName());
                            return a;
                        }).toList()
                );
            }
            dto.setArtists(artistDtos);

            var album = track.getAlbum();
            AlbumSimpleDto a = new AlbumSimpleDto();
            a.setId(album.getId());
            a.setTitle(album.getTitle());
            a.setImage(album.getImage());
            a.setReleaseDate(album.getReleaseDate());
            dto.setAlbum(a);

            dto.setPlayedAt(ev.getPlayedAt());

            return dto;
        }).toList();

        Map<UUID, HistoryResponseDto> distinctMap = dtos.stream()
                .collect(Collectors.toMap(
                        HistoryResponseDto::getTrackId,
                        dto -> dto,
                        (existing, duplicate) -> existing,
                        LinkedHashMap::new
                ));
        return new ArrayList<>(distinctMap.values());
    }


    public List<TopTrackDto> getTopTracksForArtist(UUID artistId, int limit) {
        var top = historyRepo.findTopTracksByArtist(artistId, PageRequest.of(0, Math.max(1, limit)));
        return top.stream().map(p -> {
            Track t = trackRepo.findByIdAndDeletedFalse(p.getTrackId())
                    .orElseThrow(() -> new NotFoundException("Track not found with ID: " + p.getTrackId()));

            TrackResponseDto tr = new TrackResponseDto();
            tr.setId(t.getId());
            tr.setTitle(t.getTitle());
            tr.setDuration(t.getDuration() == null ? 0 : t.getDuration());
            tr.setAudio(t.getAudio());
            tr.setCreatedAt(t.getCreatedAt());
            tr.setUpdatedAt(t.getUpdatedAt());
            tr.setAlbum(albumMapper.toSimpleDto(t.getAlbum()));

            return new TopTrackDto(tr, p.getPlayCount());
        }).toList();
    }
    public Long getArtistMonthlyListeners(UUID artistId) {
        Instant since = Instant.now().minus(30, ChronoUnit.DAYS);

        return historyRepo.countDistinctUserIdByTrack_Artist_IdAndPlayedAtAfter(artistId, since);
    }

    public List<TopTrackDto> getUserTopTracksLast30(UUID userId) {
        Instant since = Instant.now().minus(WINDOW_DAYS, ChronoUnit.DAYS);
        List<Tuple> rows = historyRepo.findTopTracksForUserSince(userId, since, PageRequest.of(0, LIMIT));
        List<UUID> ids = rows.stream().map(t -> t.get("trackId", UUID.class)).toList();
        Map<UUID, Track> trackMap = trackRepo.findByIdInAndDeletedFalse(ids).stream()
                .collect(Collectors.toMap(Track::getId, t -> t));
        List<TopTrackDto> result = new ArrayList<>(rows.size());
        for (Tuple t : rows) {
            UUID trackId = t.get("trackId", UUID.class);
            long count = t.get("playCount", Long.class);
            Track entity = trackMap.get(trackId);
            if (entity == null) continue;
            TrackResponseDto tr = new TrackResponseDto();
            tr.setId(entity.getId());
            tr.setTitle(entity.getTitle());
            tr.setDuration(entity.getDuration() == null ? 0 : entity.getDuration());
            tr.setAudio(entity.getAudio());
            tr.setCreatedAt(entity.getCreatedAt());
            tr.setUpdatedAt(entity.getUpdatedAt());
            tr.setAlbum(albumMapper.toSimpleDto(entity.getAlbum()));
            result.add(new TopTrackDto(tr, count));
        }
        return result;
    }

    public List<TopArtistDto> getUserTopArtistsLast30(UUID userId, boolean includeFeatured) {
        Instant since = Instant.now().minus(WINDOW_DAYS, ChronoUnit.DAYS);
        Map<UUID, Long> counts = new HashMap<>();
        List<Tuple> main = historyRepo.findTopArtistsForUserSince(userId, since, PageRequest.of(0, LIMIT));
        for (Tuple t : main) {
            counts.merge(t.get("artistId", UUID.class), t.get("playCount", Long.class), Long::sum);
        }
        if (includeFeatured) {
            List<Tuple> feat = historyRepo.findTopOtherArtistsForUserSince(userId, since, PageRequest.of(0, LIMIT));
            for (Tuple t : feat) {
                counts.merge(t.get("artistId", UUID.class), t.get("playCount", Long.class), Long::sum);
            }
        }
        List<Map.Entry<UUID, Long>> sorted = counts.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(LIMIT)
                .toList();
        List<UUID> artistIds = sorted.stream().map(Map.Entry::getKey).toList();
        Map<UUID, Artist> artistMap = artistRepo.findByIdInAndDeletedFalse(artistIds).stream()
                .collect(Collectors.toMap(Artist::getId, a -> a));
        List<TopArtistDto> result = new ArrayList<>(sorted.size());
        for (var e : sorted) {
            Artist a = artistMap.get(e.getKey());
            if (a == null) continue;
            ArtistSimpleDto as = new ArtistSimpleDto();
            as.setId(a.getId());
            as.setName(a.getName());
            as.setImage(a.getImage());
            result.add(new TopArtistDto(as, e.getValue()));
        }
        return result;
    }

    public List<TopTrackDto> getTopTracksAllTime(int limit) {
        var rows = historyRepo.findTopTracksAllTime(PageRequest.of(0, Math.max(1, limit)));
        List<UUID> ids = rows.stream().map(t -> t.get("trackId", UUID.class)).toList();
        Map<UUID, Track> trackMap = trackRepo.findByIdInAndDeletedFalse(ids).stream()
                .collect(Collectors.toMap(Track::getId, t -> t));

        List<TopTrackDto> result = new ArrayList<>();
        for (Tuple row : rows) {
            UUID trackId = row.get("trackId", UUID.class);
            long count = row.get("playCount", Long.class);
            Track entity = trackMap.get(trackId);
            if (entity == null) continue;

            TrackResponseDto tr = new TrackResponseDto();
            tr.setId(entity.getId());
            tr.setTitle(entity.getTitle());
            tr.setDuration(entity.getDuration() == null ? 0 : entity.getDuration());
            tr.setAudio(entity.getAudio());
            tr.setCreatedAt(entity.getCreatedAt());
            tr.setUpdatedAt(entity.getUpdatedAt());
            tr.setAlbum(albumMapper.toSimpleDto(entity.getAlbum()));

            result.add(new TopTrackDto(tr, count));
        }
        return result;
    }

    public List<TopArtistDto> getTopArtistsAllTime(int limit) {
        var rows = historyRepo.findTopArtistsAllTime(PageRequest.of(0, Math.max(1, limit)));
        List<UUID> artistIds = rows.stream().map(t -> t.get("artistId", UUID.class)).toList();
        Map<UUID, Artist> artistMap = artistRepo.findByIdInAndDeletedFalse(artistIds).stream()
                .collect(Collectors.toMap(Artist::getId, a -> a));

        List<TopArtistDto> result = new ArrayList<>();
        for (Tuple row : rows) {
            UUID artistId = row.get("artistId", UUID.class);
            long count = row.get("playCount", Long.class);
            Artist entity = artistMap.get(artistId);
            if (entity == null) continue;

            ArtistSimpleDto as = new ArtistSimpleDto();
            as.setId(entity.getId());
            as.setName(entity.getName());
            as.setImage(entity.getImage());

            result.add(new TopArtistDto(as, count));
        }
        return result;
    }

    public List<AlbumResponseDto> getAlbumsWithTopTracksAllTime(int limit) {
        var rows = historyRepo.findTopTracksAllTime(PageRequest.of(0, limit * 10));
        List<UUID> trackIds = rows.stream()
                .map(t -> t.get("trackId", UUID.class))
                .toList();

        Map<UUID, Track> trackMap = trackRepo.findByIdInAndDeletedFalse(trackIds)
                .stream()
                .collect(Collectors.toMap(Track::getId, t -> t));

        Set<UUID> albumIds = new LinkedHashSet<>();
        List<AlbumResponseDto> albums = new ArrayList<>();

        for (UUID tid : trackIds) {
            Track tr = trackMap.get(tid);
            if (tr == null || tr.getAlbum() == null) continue;

            UUID aid = tr.getAlbum().getId();
            if (albumIds.add(aid)) {
                albums.add(albumMapper.toDto(tr.getAlbum()));
                if (albums.size() >= limit) break;
            }
        }
        return albums;
    }


}
