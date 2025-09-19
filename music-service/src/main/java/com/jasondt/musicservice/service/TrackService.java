package com.jasondt.musicservice.service;

import com.jasondt.musicservice.dto.TrackCreateDto;
import com.jasondt.musicservice.dto.TrackResponseDto;
import com.jasondt.musicservice.dto.TrackUpdateDto;
import com.jasondt.musicservice.exception.DatabaseException;
import com.jasondt.musicservice.exception.NotFoundException;
import com.jasondt.musicservice.mapper.TrackMapper;
import com.jasondt.musicservice.model.Album;
import com.jasondt.musicservice.model.Artist;
import com.jasondt.musicservice.model.Track;
import com.jasondt.musicservice.repository.AlbumRepository;
import com.jasondt.musicservice.repository.TrackRepository;
import com.jasondt.musicservice.repository.ArtistRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TrackService {

    private final TrackRepository trackRepo;
    private final AlbumRepository albumRepo;
    private final TrackMapper trackMapper;
    private final ArtistRepository artistRepo;

    @Transactional
    public TrackResponseDto addTrack(TrackCreateDto dto) {
        try {
            Album album = albumRepo.findByIdAndDeletedFalse(dto.getAlbumId())
                    .orElseThrow(() -> new NotFoundException("Album not found with ID: " + dto.getAlbumId()));

            Artist artist = album.getArtist();
            Track track = trackMapper.toEntity(dto);
            track.setAlbum(album);
            track.setArtist(artist);

            if (dto.getArtistIds() != null && !dto.getArtistIds().isEmpty()) {
                List<Artist> others = new ArrayList<>();
                for (UUID aid : dto.getArtistIds()) {
                    Artist a = artistRepo.findByIdAndDeletedFalse(aid)
                            .orElseThrow(() -> new NotFoundException("Artist not found with ID: " + aid));
                    if (a.getId().equals(artist.getId())) continue;
                    if (others.stream().anyMatch(x -> x.getId().equals(a.getId()))) continue;
                    others.add(a);
                }
                track.setOtherArtists(others);
            }

            Track saved = trackRepo.save(track);
            album.addTrack(saved);
            return trackMapper.toDto(saved);
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to add track", e);
        }
    }

    public List<TrackResponseDto> getAll() {
        return trackMapper.toDto(trackRepo.findAllByDeletedFalse());
    }

    public TrackResponseDto updateTrack(UUID id, TrackUpdateDto dto) {
        Track track = trackRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Track not found with ID: " + id));
        try {
            if (dto.getTitle() != null) {
                track.setTitle(dto.getTitle());
            }

            if (dto.getDuration() != null) {
                track.setDuration(dto.getDuration());
            }
            if (dto.getAudio() != null) {
                track.setAudio(dto.getAudio());
            }
            if (dto.getAlbumId() != null) {
                Album album = albumRepo.findByIdAndDeletedFalse(dto.getAlbumId())
                        .orElseThrow(() -> new NotFoundException("Album not found with ID: " + dto.getAlbumId()));
                track.setAlbum(album);
                track.setArtist(album.getArtist());
            }
            if (dto.getArtistIds() != null) {
                List<Artist> others = new ArrayList<>();
                Artist primary = track.getArtist();
                for (UUID aid : dto.getArtistIds()) {
                    Artist a = artistRepo.findByIdAndDeletedFalse(aid)
                            .orElseThrow(() -> new NotFoundException("Artist not found with ID: " + aid));
                    if (primary != null && a.getId().equals(primary.getId())) continue;
                    if (others.stream().anyMatch(x -> x.getId().equals(a.getId()))) continue;
                    others.add(a);
                }
                track.setOtherArtists(others);
            }
            Track saved = trackRepo.save(track);
            return trackMapper.toDto(saved);
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to update track", e);
        }
    }

    public void deleteTrack(UUID id) {
        Track track = trackRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Cannot delete. Track not found with ID: " + id));
        try {
            track.setDeleted(true);
            trackRepo.save(track);
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to delete track", e);
        }
    }
}
