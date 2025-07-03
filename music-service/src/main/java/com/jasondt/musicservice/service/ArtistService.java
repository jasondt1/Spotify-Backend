package com.jasondt.musicservice.service;

import com.jasondt.musicservice.dto.ArtistRequestDto;
import com.jasondt.musicservice.dto.ArtistResponseDto;
import com.jasondt.musicservice.exception.DatabaseException;
import com.jasondt.musicservice.exception.NotFoundException;
import com.jasondt.musicservice.mapper.ArtistMapper;
import com.jasondt.musicservice.model.Artist;
import com.jasondt.musicservice.model.Genre;
import com.jasondt.musicservice.repository.ArtistRepository;
import com.jasondt.musicservice.repository.GenreRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepo;
    private final GenreRepository genreRepo;
    private final ArtistMapper artistMapper;

    public ArtistResponseDto createArtist(ArtistRequestDto dto) {
        try {
            Genre genre = genreRepo.findById(dto.getGenreId())
                    .orElseThrow(() -> new NotFoundException("Genre not found with ID: " + dto.getGenreId()));

            Artist artist = artistMapper.toEntity(dto);
            artist.setGenre(genre);

            return artistMapper.toDto(artistRepo.save(artist));
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to create artist", e);
        }
    }

    public ArtistResponseDto getArtist(UUID id) {
        Artist artist = artistRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Artist not found with ID: " + id));
        return artistMapper.toDto(artist);
    }

    public List<ArtistResponseDto> getAll() {
        return artistMapper.toDto(artistRepo.findAll());
    }

    public void deleteArtist(UUID id) {
        if (!artistRepo.existsById(id)) {
            throw new NotFoundException("Cannot delete. Artist not found with ID: " + id);
        }
        try {
            artistRepo.deleteById(id);
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to delete artist", e);
        }
    }
}