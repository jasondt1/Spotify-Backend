package com.jasondt.musicservice.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class ArtistSimpleDto {
    private UUID id;
    private String name;
    private GenreResponseDto genre;
    private String image;
    private Instant createdAt;
    private Instant updatedAt;
}
