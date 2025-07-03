package com.jasondt.musicservice.service;

import com.jasondt.musicservice.dto.GenreRequestDto;
import com.jasondt.musicservice.dto.GenreResponseDto;
import com.jasondt.musicservice.exception.DatabaseException;
import com.jasondt.musicservice.exception.NotFoundException;
import com.jasondt.musicservice.mapper.GenreMapper;
import com.jasondt.musicservice.model.Genre;
import com.jasondt.musicservice.repository.GenreRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GenreService {

    private final GenreRepository genreRepo;
    private final GenreMapper genreMapper;

    public GenreResponseDto create(GenreRequestDto dto) {
        try {
            Genre genre = genreMapper.toEntity(dto);
            return genreMapper.toResponseDto(genreRepo.save(genre));
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to create genre", e);
        }
    }

    public List<GenreResponseDto> getAll() {
        return genreMapper.toResponseDto(genreRepo.findAll());
    }

    public GenreResponseDto getById(UUID id) {
        Genre genre = genreRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Genre not found with ID: " + id));
        return genreMapper.toResponseDto(genre);
    }

    public void delete(UUID id) {
        if (!genreRepo.existsById(id)) {
            throw new NotFoundException("Cannot delete. Genre not found with ID: " + id);
        }
        try {
            genreRepo.deleteById(id);
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to delete genre", e);
        }
    }
}