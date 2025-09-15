package com.jasondt.musicservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ArtistUpdateDto {
    private String name;
    private UUID genreId;
    private String image;
}

