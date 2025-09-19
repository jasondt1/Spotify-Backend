package com.jasondt.musicservice.mapper;

import com.jasondt.musicservice.dto.AlbumSimpleDto;
import com.jasondt.musicservice.dto.ArtistSimpleDto;
import com.jasondt.musicservice.dto.GenreResponseDto;
import com.jasondt.musicservice.dto.PlaylistSimpleDto;
import com.jasondt.musicservice.model.Album;
import com.jasondt.musicservice.model.Artist;
import com.jasondt.musicservice.model.Genre;
import com.jasondt.musicservice.model.Playlist;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class SimpleMapper {

    public abstract ArtistSimpleDto toArtistSimpleDto(Artist entity);

    public abstract GenreResponseDto toGenreResponseDto(Genre entity);

    @Mapping(target = "artist", source = "artist")
    public abstract AlbumSimpleDto toAlbumSimpleDto(Album entity);

    public abstract PlaylistSimpleDto toPlaylistSimpleDto(Playlist entity);

    @AfterMapping
    protected void afterPlaylistMap(Playlist entity, @MappingTarget PlaylistSimpleDto dto) {
        // Keep owner lightweight by default; callers may enrich if needed
        dto.setOwner(null);
    }
}

