package com.jasondt.musicservice.mapper;

import com.jasondt.musicservice.dto.AlbumCreateDto;
import com.jasondt.musicservice.dto.AlbumResponseDto;
import com.jasondt.musicservice.dto.AlbumSimpleDto;
import com.jasondt.musicservice.model.Album;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = { TrackMapper.class })
public abstract class AlbumMapper {
    public abstract AlbumResponseDto toDto(Album entity);
    public abstract List<AlbumResponseDto> toDto(List<Album> entityList);
    public abstract Album toEntity(AlbumCreateDto dto);
    public abstract AlbumSimpleDto toSimpleDto(Album album);
}
