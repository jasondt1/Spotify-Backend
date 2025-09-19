package com.jasondt.musicservice.mapper;

import com.jasondt.musicservice.dto.*;
import com.jasondt.musicservice.model.*;
import org.mapstruct.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class LibraryMapper {

    public abstract LibraryResponseDto toDto(Library library);

    public List<PlaylistSimpleDto> toPlaylistSimpleDtoList(List<LibraryPlaylist> playlists) {
        return playlists.stream()
                .sorted(Comparator.comparing(LibraryPlaylist::getLastPlayedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(LibraryPlaylist::getPlaylist)
                .map(this::toPlaylistSimpleDto)
                .collect(Collectors.toList());
    }
    public List<AlbumSimpleDto> toAlbumSimpleDtoList(List<LibraryAlbum> albums) {
        return albums.stream()
                .sorted(Comparator.comparing(LibraryAlbum::getLastPlayedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(LibraryAlbum::getAlbum)
                .map(this::toAlbumSimpleDto)
                .collect(Collectors.toList());
    }

    public abstract PlaylistSimpleDto toPlaylistSimpleDto(Playlist entity);
    public abstract AlbumSimpleDto toAlbumSimpleDto(Album entity);

    protected abstract ArtistSimpleDto toArtistSimpleDto(Artist entity);
    protected abstract GenreResponseDto toGenreResponseDto(Genre entity);

    @AfterMapping
    protected void afterPlaylistMap(Playlist entity, @MappingTarget PlaylistSimpleDto dto) {
        dto.setOwner(null);
    }
}
