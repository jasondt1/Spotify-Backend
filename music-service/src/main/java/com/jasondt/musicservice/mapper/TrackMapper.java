package com.jasondt.musicservice.mapper;

import com.jasondt.musicservice.dto.TrackRequestDto;
import com.jasondt.musicservice.dto.TrackResponseDto;
import com.jasondt.musicservice.model.Track;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrackMapper {
    TrackResponseDto toDto(Track entity);
    List<TrackResponseDto> toDto(List<Track> entityList);
    Track toEntity(TrackRequestDto dto);
}