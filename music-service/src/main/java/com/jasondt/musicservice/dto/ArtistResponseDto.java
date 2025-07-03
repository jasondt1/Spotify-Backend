package com.jasondt.musicservice.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ArtistResponseDto {
    private UUID id;
    private String name;
    private GenreResponseDto genre;
    private List<AlbumResponseDto> albums;
    private List<TrackResponseDto> tracks;
}