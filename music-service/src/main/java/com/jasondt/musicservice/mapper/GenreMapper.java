package com.jasondt.musicservice.mapper;

import com.jasondt.musicservice.dto.GenreCreateDto;
import com.jasondt.musicservice.dto.GenreResponseDto;
import com.jasondt.musicservice.model.Genre;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    GenreResponseDto toResponseDto(Genre entity);
    List<GenreResponseDto> toResponseDto(List<Genre> entityList);
    Genre toEntity(GenreCreateDto dto);
}
