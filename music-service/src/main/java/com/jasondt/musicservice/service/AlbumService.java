package com.jasondt.musicservice.service;

import com.jasondt.musicservice.dto.AlbumRequestDto;
import com.jasondt.musicservice.dto.AlbumResponseDto;
import com.jasondt.musicservice.exception.DatabaseException;
import com.jasondt.musicservice.exception.NotFoundException;
import com.jasondt.musicservice.mapper.AlbumMapper;
import com.jasondt.musicservice.model.Album;
import com.jasondt.musicservice.model.Artist;
import com.jasondt.musicservice.repository.AlbumRepository;
import com.jasondt.musicservice.repository.ArtistRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepo;
    private final ArtistRepository artistRepo;
    private final AlbumMapper albumMapper;

    public AlbumResponseDto createAlbum(AlbumRequestDto dto) {
        try {
            Artist artist = artistRepo.findById(dto.getArtistId())
                    .orElseThrow(() -> new NotFoundException("Artist not found with ID: " + dto.getArtistId()));

            Album album = albumMapper.toEntity(dto);
            album.setArtist(artist);

            artist.addAlbum(album);
            artistRepo.save(artist);

            return albumMapper.toDto(album);
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to create album", e);
        }
    }

    public AlbumResponseDto getAlbum(UUID id) {
        Album album = albumRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Album not found with ID: " + id));
        return albumMapper.toDto(album);
    }

    public List<AlbumResponseDto> getAll() {
        return albumMapper.toDto(albumRepo.findAll());
    }
}