package com.jasondt.musicservice.service;

import com.jasondt.musicservice.client.UserClient;
import com.jasondt.musicservice.dto.*;
import com.jasondt.musicservice.exception.DatabaseException;
import com.jasondt.musicservice.exception.DuplicateTrackException;
import com.jasondt.musicservice.exception.NotFoundException;
import com.jasondt.musicservice.mapper.PlaylistMapper;
import com.jasondt.musicservice.model.Playlist;
import com.jasondt.musicservice.model.PlaylistTrack;
import com.jasondt.musicservice.model.Track;
import com.jasondt.musicservice.repository.PlaylistRepository;
import com.jasondt.musicservice.repository.PlaylistTrackRepository;
import com.jasondt.musicservice.repository.TrackRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepo;
    private final PlaylistTrackRepository playlistTrackRepo;
    private final TrackRepository trackRepo;
    private final PlaylistMapper playlistMapper;
    private final UserClient userClient;
    private final LibraryService libraryService;

    @Transactional
    public PlaylistResponseDto create(UUID ownerId, PlaylistCreateDto dto) {
        try {
            Playlist playlist = new Playlist();
            playlist.setOwnerId(ownerId);
            playlist.setName(dto.getName());
            playlist.setDescription(dto.getDescription());
            playlist.setImage(dto.getImage());

            if (dto.getTrackIds() != null && !dto.getTrackIds().isEmpty()) {
                int pos = 0;
                for (UUID trackId : dto.getTrackIds()) {
                    Track track = trackRepo.findByIdAndDeletedFalse(trackId)
                            .orElseThrow(() -> new NotFoundException("Track not found with ID: " + trackId));
                    PlaylistTrack pt = new PlaylistTrack();
                    pt.setTrack(track);
                    pt.setPosition(pos++);
                    playlist.addPlaylistTrack(pt);
                }
            }

            Playlist saved = playlistRepo.save(playlist);
            // auto-add to owner's library (best-effort)
            try { libraryService.addPlaylist(ownerId, saved.getId()); } catch (Exception ignore) {}
            return playlistMapper.toDto(saved);
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to create playlist", e);
        }
    }

    

    public PlaylistResponseDto getById(UUID id) {
        Playlist p = playlistRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Playlist not found with ID: " + id));

        PlaylistResponseDto dto = playlistMapper.toDto(p);

        try {
            UserResponseDto user = userClient.getUserById(p.getOwnerId());
            dto.setOwner(user);
        } catch (Exception e) {
            dto.setOwner(null);
        }

        return dto;
    }

    public List<PlaylistResponseDto> getMine(UUID ownerId) {
        List<Playlist> playlists = playlistRepo.findAllByOwnerIdAndDeletedFalse(ownerId);
        List<PlaylistResponseDto> dtos = playlistMapper.toDto(playlists);

        try {
            UserResponseDto user = userClient.getUserById(ownerId);
            for (PlaylistResponseDto dto : dtos) {
                dto.setOwner(user);
            }
        } catch (Exception e) {
            for (PlaylistResponseDto dto : dtos) {
                dto.setOwner(null);
            }
        }

        return dtos;
    }


    @Transactional
    public PlaylistResponseDto update(UUID id, UUID ownerId, PlaylistUpdateDto dto) {
        Playlist p = playlistRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Playlist not found with ID: " + id));
        if (!p.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Playlist not found with ID: " + id);
        }
        try {
            if (dto.getName() != null) p.setName(dto.getName());
            if (dto.getDescription() != null) p.setDescription(dto.getDescription());
            if (dto.getImage() != null) p.setImage(dto.getImage());
            return playlistMapper.toDto(playlistRepo.save(p));
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to update playlist", e);
        }
    }

    @Transactional
    public void delete(UUID id, UUID ownerId) {
        Playlist p = playlistRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Playlist not found with ID: " + id));
        if (!p.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Playlist not found with ID: " + id);
        }
        try {
            p.setDeleted(true);
            playlistRepo.save(p);
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to delete playlist", e);
        }
    }

    @Transactional
    public PlaylistResponseDto addTracks(UUID id, UUID ownerId, UUID trackId) {
        Playlist p = playlistRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Playlist not found with ID: " + id));
        if (!p.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Playlist not found with ID: " + id);
        }
        try {
            boolean exists = p.getTracks() != null && p.getTracks().stream()
                    .anyMatch(pt -> pt.getTrack().getId().equals(trackId));
            if (exists) {
                throw new DuplicateTrackException("Track already exists in playlist");
            }

            int nextPos = p.getTracks() == null ? 0 : p.getTracks().size();
            Track track = trackRepo.findByIdAndDeletedFalse(trackId)
                    .orElseThrow(() -> new NotFoundException("Track not found with ID: " + trackId));
            PlaylistTrack pt = new PlaylistTrack();
            pt.setTrack(track);
            pt.setPosition(nextPos);
            p.addPlaylistTrack(pt);
            return playlistMapper.toDto(playlistRepo.save(p));
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to add tracks to playlist", e);
        }
    }

    @Transactional
    public void removeTrack(UUID id, UUID ownerId, UUID trackId) {
        Playlist p = playlistRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Playlist not found with ID: " + id));
        if (!p.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Playlist not found with ID: " + id);
        }
        try {
            var opt = playlistTrackRepo.findByPlaylist_IdAndTrack_IdAndDeletedFalse(id, trackId);
            if (opt.isEmpty()) return;
            PlaylistTrack pt = opt.get();
            pt.setDeleted(true);
            playlistTrackRepo.save(pt);
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to remove track from playlist", e);
        }
    }
}
