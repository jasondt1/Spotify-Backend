package com.jasondt.musicservice.mapper;

import com.jasondt.musicservice.dto.ArtistRequestDto;
import com.jasondt.musicservice.dto.ArtistResponseDto;
import com.jasondt.musicservice.model.Artist;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArtistMapper {
    ArtistResponseDto toDto(Artist entity);
    List<ArtistResponseDto> toDto(List<Artist> entityList);
    Artist toEntity(ArtistRequestDto dto);
}