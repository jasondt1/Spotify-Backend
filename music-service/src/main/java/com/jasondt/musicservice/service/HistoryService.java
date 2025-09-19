package com.jasondt.musicservice.service;

import com.jasondt.musicservice.dto.*;
import com.jasondt.musicservice.exception.NotFoundException;
import com.jasondt.musicservice.mapper.AlbumMapper;
import com.jasondt.musicservice.model.History;
import com.jasondt.musicservice.model.Track;
import com.jasondt.musicservice.repository.HistoryRepository;
import com.jasondt.musicservice.repository.TrackRepository;
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
}
