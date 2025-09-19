package com.jasondt.musicservice.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class AlbumSimpleDto {
    private UUID id;
    private String title;
    private String image;
    private LocalDate releaseDate;
    private ArtistSimpleDto artist;
}

