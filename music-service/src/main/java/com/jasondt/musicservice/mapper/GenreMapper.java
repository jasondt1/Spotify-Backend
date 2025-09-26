package com.jasondt.musicservice.mapper;

import com.jasondt.musicservice.dto.GenreCreateDto;
import com.jasondt.musicservice.dto.GenreResponseDto;
import com.jasondt.musicservice.model.Genre;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class GenreMapper {
    public abstract GenreResponseDto toResponseDto(Genre entity);
    public abstract List<GenreResponseDto> toResponseDto(List<Genre> entityList);
    public abstract Genre toEntity(GenreCreateDto dto);
}
