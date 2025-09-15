package com.jasondt.musicservice.service;

import com.jasondt.musicservice.dto.GenreCreateDto;
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

    public GenreResponseDto create(GenreCreateDto dto) {
        try {
            Genre genre = genreMapper.toEntity(dto);
            return genreMapper.toResponseDto(genreRepo.save(genre));
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to create genre", e);
        }
    }

    public List<GenreResponseDto> getAll() {
        return genreMapper.toResponseDto(genreRepo.findAllByDeletedFalse());
    }

    public GenreResponseDto getById(UUID id) {
        Genre genre = genreRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Genre not found with ID: " + id));
        return genreMapper.toResponseDto(genre);
    }

    public GenreResponseDto update(UUID id, com.jasondt.musicservice.dto.GenreUpdateDto dto) {
        Genre genre = genreRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Genre not found with ID: " + id));
        try {
            genre.setName(dto.getName());
            return genreMapper.toResponseDto(genreRepo.save(genre));
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to update genre", e);
        }
    }

    public void delete(UUID id) {
        Genre genre = genreRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new NotFoundException("Cannot delete. Genre not found with ID: " + id));
        try {
            genre.setDeleted(true);
            genreRepo.save(genre);
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to delete genre", e);
        }
    }
}
