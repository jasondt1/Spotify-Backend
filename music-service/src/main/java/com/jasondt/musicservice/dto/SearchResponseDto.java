package com.jasondt.musicservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class SearchResponseDto {
    private TopSearchResultDto top;
    private List<ArtistSimpleDto> artists;
    private List<AlbumSimpleDto> albums;
    private List<PlaylistSimpleDto> playlists;
    private List<TrackResponseDto> tracks;
}
