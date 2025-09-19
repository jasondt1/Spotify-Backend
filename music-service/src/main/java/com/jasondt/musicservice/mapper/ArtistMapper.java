package com.jasondt.musicservice.mapper;

import com.jasondt.musicservice.dto.ArtistCreateDto;
import com.jasondt.musicservice.dto.ArtistResponseDto;
import com.jasondt.musicservice.model.Artist;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = { AlbumMapper.class })
public abstract class ArtistMapper {

    @Autowired
    protected AlbumMapper albumMapper;

    public abstract ArtistResponseDto toDto(Artist entity);
    public abstract List<ArtistResponseDto> toDto(List<Artist> entityList);
    public abstract Artist toEntity(ArtistCreateDto dto);
}
