package com.jasondt.musicservice.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class AlbumResponseDto {
    private UUID id;
    private String title;
    private LocalDate releaseDate;
    private ArtistResponseDto artist;
    private List<TrackResponseDto> tracks;
}