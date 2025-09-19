package com.jasondt.musicservice.service;

import com.jasondt.musicservice.dto.AlbumCreateDto;
import com.jasondt.musicservice.dto.AlbumResponseDto;
import com.jasondt.musicservice.dto.AlbumUpdateDto;
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

    public AlbumResponseDto createAlbum(AlbumCreateDto dto) {
        try {
            Artist artist = artistRepo.findByIdAndDeletedFalse(dto.getArtistId())
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
        Album album = albumRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Album not found with ID: " + id));
        return albumMapper.toDto(album);
    }

    public List<AlbumResponseDto> getAll() {
        return albumMapper.toDto(albumRepo.findAllByDeletedFalse());
    }

    public AlbumResponseDto updateAlbum(UUID id, AlbumUpdateDto dto) {
        Album album = albumRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Album not found with ID: " + id));
        try {
            if (dto.getTitle() != null) {
                album.setTitle(dto.getTitle());
            }
            if (dto.getReleaseDate() != null) {
                album.setReleaseDate(dto.getReleaseDate());
            }
            if (dto.getArtistId() != null) {
                Artist artist = artistRepo.findByIdAndDeletedFalse(dto.getArtistId())
                        .orElseThrow(() -> new NotFoundException("Artist not found with ID: " + dto.getArtistId()));
                album.setArtist(artist);
            }
            if(dto.getReleaseDate() != null) {

            }
            if (dto.getImage() != null) {
                String img = dto.getImage();
                album.setImage(img);
            }
            return albumMapper.toDto(albumRepo.save(album));
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to update album", e);
        }
    }

    public void deleteAlbum(UUID id) {
        Album album = albumRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Cannot delete. Album not found with ID: " + id));
        try {
            album.setDeleted(true);
            albumRepo.save(album);
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to delete album", e);
        }
    }
}
