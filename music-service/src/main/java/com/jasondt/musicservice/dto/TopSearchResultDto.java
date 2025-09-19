package com.jasondt.musicservice.dto;

import lombok.Data;

@Data
public class TopSearchResultDto {
    private String type;
    private ArtistSimpleDto artist;
    private AlbumSimpleDto album;
    private TrackResponseDto track;
    private PlaylistSimpleDto playlist;
}

