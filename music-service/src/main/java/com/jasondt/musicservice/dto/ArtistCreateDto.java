package com.jasondt.musicservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ArtistCreateDto {
    private String name;
    private UUID genreId;
    private String image;
}
