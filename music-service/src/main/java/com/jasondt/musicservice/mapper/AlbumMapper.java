package com.jasondt.musicservice.mapper;

import com.jasondt.musicservice.dto.AlbumCreateDto;
import com.jasondt.musicservice.dto.AlbumResponseDto;
import com.jasondt.musicservice.dto.AlbumSimpleDto;
import com.jasondt.musicservice.model.Album;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = { TrackMapper.class })
public interface AlbumMapper {
    AlbumResponseDto toDto(Album entity);
    List<AlbumResponseDto> toDto(List<Album> entityList);
    Album toEntity(AlbumCreateDto dto);
    AlbumSimpleDto toSimpleDto(Album album);
}
