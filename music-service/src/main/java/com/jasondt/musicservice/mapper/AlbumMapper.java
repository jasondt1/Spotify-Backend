package com.jasondt.musicservice.mapper;

import com.jasondt.musicservice.dto.AlbumRequestDto;
import com.jasondt.musicservice.dto.AlbumResponseDto;
import com.jasondt.musicservice.model.Album;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AlbumMapper {
    AlbumResponseDto toDto(Album entity);
    List<AlbumResponseDto> toDto(List<Album> entityList);
    Album toEntity(AlbumRequestDto dto);
}