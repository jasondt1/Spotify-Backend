package com.jasondt.musicservice.service;

import com.jasondt.musicservice.dto.TrackRequestDto;
import com.jasondt.musicservice.dto.TrackResponseDto;
import com.jasondt.musicservice.exception.DatabaseException;
import com.jasondt.musicservice.exception.NotFoundException;
import com.jasondt.musicservice.mapper.TrackMapper;
import com.jasondt.musicservice.model.Album;
import com.jasondt.musicservice.model.Artist;
import com.jasondt.musicservice.model.Track;
import com.jasondt.musicservice.repository.AlbumRepository;
import com.jasondt.musicservice.repository.TrackRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TrackService {

    private final TrackRepository trackRepo;
    private final AlbumRepository albumRepo;
    private final TrackMapper trackMapper;

    public TrackResponseDto addTrack(TrackRequestDto dto) {
        try {
            Album album = albumRepo.findById(dto.getAlbumId())
                    .orElseThrow(() -> new NotFoundException("Album not found with ID: " + dto.getAlbumId()));

            Artist artist = album.getArtist();
            Track track = trackMapper.toEntity(dto);
            track.setAlbum(album);
            track.setArtist(artist);

            album.addTrack(track);
            albumRepo.save(album);

            return trackMapper.toDto(track);
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to add track", e);
        }
    }

    public List<TrackResponseDto> getAll() {
        return trackMapper.toDto(trackRepo.findAll());
    }
}
