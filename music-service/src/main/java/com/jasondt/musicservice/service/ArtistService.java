package com.jasondt.musicservice.service;

import com.jasondt.musicservice.dto.ArtistCreateDto;
import com.jasondt.musicservice.dto.ArtistResponseDto;
import com.jasondt.musicservice.dto.ArtistUpdateDto;
import com.jasondt.musicservice.exception.DatabaseException;
import com.jasondt.musicservice.exception.NotFoundException;
import com.jasondt.musicservice.mapper.ArtistMapper;
import com.jasondt.musicservice.model.Artist;
import com.jasondt.musicservice.model.Genre;
import com.jasondt.musicservice.repository.ArtistRepository;
import com.jasondt.musicservice.repository.GenreRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataAccessException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepo;
    private final GenreRepository genreRepo;
    private final ArtistMapper artistMapper;

    @CacheEvict(value = "allArtists", allEntries = true)
    public ArtistResponseDto createArtist(ArtistCreateDto dto) {
        try {
            Genre genre = genreRepo.findByIdAndDeletedFalse(dto.getGenreId())
                    .orElseThrow(() -> new NotFoundException("Genre not found with ID: " + dto.getGenreId()));

            Artist artist = artistMapper.toEntity(dto);
            artist.setGenre(genre);

            return artistMapper.toDto(artistRepo.save(artist));
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to create artist", e);
        }
    }

    @Cacheable(value = "artists", key = "#id")
    public ArtistResponseDto getArtist(UUID id) {
        Artist artist = artistRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Artist not found with ID: " + id));
        return artistMapper.toDto(artist);
    }

    @CachePut(value = "artists", key = "#id")
    @CacheEvict(value = "allArtists", allEntries = true)
    public ArtistResponseDto updateArtist(UUID id, ArtistUpdateDto dto) {
        Artist artist = artistRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Artist not found with ID: " + id));
        try {
            if (dto.getName() != null) {
                artist.setName(dto.getName());
            }
            if (dto.getGenreId() != null) {
                Genre genre = genreRepo.findByIdAndDeletedFalse(dto.getGenreId())
                        .orElseThrow(() -> new NotFoundException("Genre not found with ID: " + dto.getGenreId()));
                artist.setGenre(genre);
            }
            if (dto.getImage() != null) {
                String img = dto.getImage();
                artist.setImage(img);
            }
            if (dto.getCoverImage() != null) {
                artist.setCoverImage(dto.getCoverImage());
            }
            return artistMapper.toDto(artistRepo.save(artist));
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to update artist", e);
        }
    }

    @Cacheable(value = "allArtists")
    public List<ArtistResponseDto> getAll() {
        return artistMapper.toDto(artistRepo.findAllByDeletedFalseOrderByCreatedAtAsc());
    }

    @Caching(evict = {
        @CacheEvict(value = "artists", key = "#id"),
        @CacheEvict(value = "allArtists", allEntries = true)
    })
    public void deleteArtist(UUID id) {
        Artist artist = artistRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Cannot delete. Artist not found with ID: " + id));
        try {
            artist.setDeleted(true);
            artistRepo.save(artist);
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to delete artist", e);
        }
    }
}
