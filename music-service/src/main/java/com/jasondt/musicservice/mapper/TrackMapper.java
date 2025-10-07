package com.jasondt.musicservice.mapper;

import com.jasondt.musicservice.dto.*;
import com.jasondt.musicservice.model.Artist;
import com.jasondt.musicservice.model.Track;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class TrackMapper {

    public abstract TrackResponseDto toDto(Track entity);
    public abstract List<TrackResponseDto> toDto(List<Track> entityList);
    public abstract Track toEntity(TrackCreateDto dto);

    protected abstract ArtistSimpleDto toDto(Artist entity);

    @AfterMapping
    protected void afterMap(Track entity, @MappingTarget TrackResponseDto dto) {
        if (entity == null) return;
        List<ArtistSimpleDto> list = new ArrayList<>();
        if (entity.getArtist() != null && !entity.getArtist().isDeleted()) {
            list.add(toDto(entity.getArtist()));
        }
        if (entity.getOtherArtists() != null) {
            for (Artist a : entity.getOtherArtists()) {
                if (a != null && !a.isDeleted()) {
                    if (entity.getArtist() != null && a.getId() != null && a.getId().equals(entity.getArtist().getId())) {
                        continue;
                    }
                    list.add(toDto(a));
                }
            }
        }
        dto.setArtists(list);
        if (entity.getAlbum() != null) {
            AlbumSimpleDto a = new AlbumSimpleDto();
            a.setId(entity.getAlbum().getId());
            a.setTitle(entity.getAlbum().getTitle());
            a.setImage(entity.getAlbum().getImage());
            a.setReleaseDate(entity.getAlbum().getReleaseDate());
            dto.setAlbum(a);
        }

        dto.setLyrics(null);
    }
}
