package com.jasondt.musicservice.mapper;

import com.jasondt.musicservice.dto.*;
import com.jasondt.musicservice.model.Artist;
import com.jasondt.musicservice.model.LyricsLine;
import com.jasondt.musicservice.model.Track;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class TrackMapper {

    public TrackResponseDto toDto(Track entity) { return toDto(entity, false); }
    public List<TrackResponseDto> toDto(List<Track> entityList) {
        if (entityList == null) return null;
        List<TrackResponseDto> out = new ArrayList<>();
        for (Track t : entityList) out.add(toDto(t, false));
        return out;
    }

    public abstract TrackResponseDto toDto(Track entity, @Context boolean includeLyrics);
    public abstract Track toEntity(TrackCreateDto dto);

    protected abstract ArtistSimpleDto toDto(Artist entity);

    @AfterMapping
    protected void afterMap(Track entity, @MappingTarget TrackResponseDto dto, @Context boolean includeLyrics) {
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
        if (includeLyrics && entity.getLyrics() != null) {
            List<LyricsLineDto> lyrics = new java.util.ArrayList<>();
            for (LyricsLine line : entity.getLyrics()) {
                if (line == null) continue;
                LyricsLineDto ld = new LyricsLineDto();
                ld.setTimestamp(line.getTimestamp());
                ld.setText(line.getText());
                lyrics.add(ld);
            }
            dto.setLyrics(lyrics);
        } else {
            dto.setLyrics(null);
        }
    }
}
