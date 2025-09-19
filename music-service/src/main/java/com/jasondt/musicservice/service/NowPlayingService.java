package com.jasondt.musicservice.service;

import com.jasondt.musicservice.dto.NowPlayingResponseDto;
import com.jasondt.musicservice.dto.TrackResponseDto;
import com.jasondt.musicservice.exception.NotFoundException;
import com.jasondt.musicservice.mapper.TrackMapper;
import com.jasondt.musicservice.model.NowPlaying;
import com.jasondt.musicservice.model.Track;
import com.jasondt.musicservice.repository.NowPlayingRepository;
import com.jasondt.musicservice.repository.TrackRepository;
import com.jasondt.musicservice.exception.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class NowPlayingService {

    private final NowPlayingRepository nowPlayingRepo;
    private final TrackRepository trackRepo;
    private final TrackMapper trackMapper;
    private final LibraryService libraryService;

    public Optional<NowPlayingResponseDto> getNowPlaying(UUID userId) {
        return nowPlayingRepo.findByUserId(userId).map(this::toDto);
    }

    @Transactional
    public TrackResponseDto setNowPlaying(UUID userId, UUID trackId) {
        Track track = trackRepo.findByIdAndDeletedFalse(trackId)
                .orElseThrow(() -> new NotFoundException("Track not found with ID: " + trackId));
        NowPlaying np = upsertNowPlaying(userId, track,
                track.getArtist() == null ? null : track.getArtist().getId(), null, null);
        nowPlayingRepo.save(np);
        return trackMapper.toDto(track);
    }

    @Transactional
    public NowPlayingResponseDto setNowPlaying(UUID userId, UUID trackId, UUID artistId, UUID albumId, UUID playlistId) {
        int provided = 0;
        if (artistId != null) provided++;
        if (albumId != null) provided++;
        if (playlistId != null) provided++;
        if (provided > 1) {
            throw new BadRequestException("Only one of artistId, albumId, playlistId may be provided");
        }
        Track track = trackRepo.findByIdAndDeletedFalse(trackId)
                .orElseThrow(() -> new NotFoundException("Track not found with ID: " + trackId));
        boolean anyProvided = artistId != null || albumId != null || playlistId != null;
        UUID finalArtist = anyProvided ? artistId : (track.getArtist() == null ? null : track.getArtist().getId());
        NowPlaying np = upsertNowPlaying(userId, track, finalArtist, albumId, playlistId);
        nowPlayingRepo.save(np);
        try {
            if (playlistId != null) {
                libraryService.markPlaylistPlayed(userId, playlistId);
            } else if (albumId != null) {
                libraryService.markAlbumPlayed(userId, albumId);
            }
        } catch (Exception ignored) {}
        return toDto(np);
    }

    @Transactional
    public void clear(UUID userId) {
        nowPlayingRepo.deleteByUserId(userId);
    }

    @Transactional
    public NowPlayingResponseDto updatePosition(UUID userId, int positionSec) {
        NowPlaying np = nowPlayingRepo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("No active playback for user"));
        np.setPositionSec(Math.max(0, positionSec));
        nowPlayingRepo.save(np);
        return toDto(np);
    }

    private NowPlaying upsertNowPlaying(UUID userId, Track track, UUID artistId, UUID albumId, UUID playlistId) {
        NowPlaying np = nowPlayingRepo.findByUserId(userId).orElseGet(() -> {
            NowPlaying created = new NowPlaying();
            created.setUserId(userId);
            return created;
        });
        np.setTrack(track);
        np.setStartedAt(Instant.now());
        np.setPositionSec(0);
        np.setArtistId(artistId);
        np.setAlbumId(albumId);
        np.setPlaylistId(playlistId);
        return np;
    }

    private NowPlayingResponseDto toDto(NowPlaying np) {
        NowPlayingResponseDto dto = new NowPlayingResponseDto();
        dto.setTrack(trackMapper.toDto(np.getTrack()));
        dto.setStartedAt(np.getStartedAt());
        dto.setPositionSec(np.getPositionSec() == null ? 0 : np.getPositionSec());
        dto.setArtistId(np.getArtistId());
        dto.setAlbumId(np.getAlbumId());
        dto.setPlaylistId(np.getPlaylistId());
        return dto;
    }
}
