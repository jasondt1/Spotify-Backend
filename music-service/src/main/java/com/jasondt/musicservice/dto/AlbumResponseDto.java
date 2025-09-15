package com.jasondt.musicservice.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class AlbumResponseDto {
    private UUID id;
    private String title;
    private LocalDate releaseDate;
    private ArtistSimpleDto artist;
    private List<TrackResponseDto> tracks;
    private String image;
    private Instant createdAt;
    private Instant updatedAt;
}
