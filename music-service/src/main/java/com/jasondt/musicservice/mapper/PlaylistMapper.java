package com.jasondt.musicservice.mapper;

import com.jasondt.musicservice.dto.PlaylistResponseDto;
import com.jasondt.musicservice.dto.TrackResponseDto;
import com.jasondt.musicservice.model.Playlist;
import com.jasondt.musicservice.model.PlaylistTrack;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = { TrackMapper.class })
public abstract class PlaylistMapper {

    @Autowired
    protected TrackMapper trackMapper;

    public abstract PlaylistResponseDto toDto(Playlist entity);
    public abstract List<PlaylistResponseDto> toDto(List<Playlist> entityList);

    @AfterMapping
    protected void mapTracks(Playlist entity, @MappingTarget PlaylistResponseDto dto) {
        if (entity == null || entity.getTracks() == null) return;

        List<TrackResponseDto> tracks = entity.getTracks().stream()
                .sorted(Comparator.comparing(
                        pt -> pt.getPosition() == null ? Integer.MAX_VALUE : pt.getPosition()
                ))
                .map(pt -> {
                    TrackResponseDto trackDto = trackMapper.toDto(pt.getTrack());
                    trackDto.setCreatedAt(pt.getCreatedAt());
                    return trackDto;
                })
                .collect(Collectors.toList());

        dto.setTracks(tracks);
    }

}

